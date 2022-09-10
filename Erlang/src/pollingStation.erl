%%%-------------------------------------------------------------------
%%% @author yuri
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 13. lug 2022 10:27
%%%-------------------------------------------------------------------
-module('pollingStation').
-author("yuri").
-include_lib("public_key/include/public_key.hrl").
-include("util.hrl").
-include("voter.hrl").
%% API
-export([start/2, ps_loop/3, init/1]).


start(Node, Id)->
  try %% TODO maybe use a local monitor instead
    Pid=spawn_link(Node, ?MODULE,init,[Id]),
     io:format("[ps~w] loop started on pid ~p ~n", [Id,Pid]),
    {ok,Pid}
  catch
      Error  -> {error,Error}
  end.

init(Id)->
  Key=util:read_key("ps_key.pem"),
  ok,SequenceNumber = crypto:strong_rand_bytes(4),
  voter:init(),
  voter:test_insert(),
  register(polling_station_endpoint, self()),
  ps_loop(Key ,binary:decode_unsigned(SequenceNumber), Id).

send_turnout(Admin)->
  Turnout=length(voter:select_all_who_voted()),
  Admin !{self(), Turnout},
  io:format("[ps ] turnout sent ~p ~n",[Admin]).


send_list(Admin) ->
  List=voter:select_all_who_voted(),
  Admin !{self(), List},
  io:format("[ps ] list ~n sent ~p ~n",[Admin]).

ps_loop(Key, N, Id) ->
  receive
    {_, ok} ->ps_loop(Key,  N, Id);
    {_,suspend_vote}-> ps_suspended(Key,N, Id);
    {Admin,get_status} ->Admin !{self(), "open"},
      io:format("[ps~w] status sent ~p ~n",[Id,Admin]),
      ps_loop(Key,  N, Id);
    {Admin, get_turnout}->
      send_turnout(Admin),
      ps_loop(Key,  N, Id);
    {Admin, get_list}->
      send_list(Admin),
      ps_loop(Key,  N, Id);
    {Admin, VoterID, search}->
      Admin ! {self(), "not_implemented"},
      io:format("[ps~w] not_implemented ~p ~n",[Id,Admin]),
      ps_loop(Key,  N, Id);
    {_Booth, Payload, vote} ->
      try
        {Vote,VoterID,VoterSign} =Payload,
        Nid=list_to_integer(VoterID),
        io:format("[ps~w] vote received:~nvoter id ~p ~n", [Id,Nid]),
        HasVoted=voter:voter_has_voted(Nid),
        io:format("[ps~w] has voted ~p ~n", [Id,HasVoted]),
        false=HasVoted,
        KeyUrl=voter:get_voter_pub_key_from_id(Nid),
        VoterKey = util:read_key(KeyUrl),
        true= public_key:verify(Vote,sha256, VoterSign,VoterKey),
        io:format("[ps~w] vote certified ~n", [Id]),
        Term=term_to_binary({Vote, N}), % append sequence number
        Signature = public_key:sign(Term, sha256, Key),
        %Signature = crypto:sign(dss, sha256,Term, Key),
        {central_station_endpoint,cs@studente75} ! {Id, Signature, {Vote, N}},
        io:format("[ps~w] vote sent ~n",[Id]),
        voter:set_voter_flag(Nid),
        ps_loop(Key,  N+1, Id)
        catch ErrorType:ErrorReason:Stacktrace ->
          io:format("[ps~w] ERROR: ~n: ",[Id]),
          io:format("~w ~n ~w ~n ~w ~n",
            [ ErrorType,ErrorReason,Stacktrace]),
          ps_loop(Key,  N, Id) end;
    _ -> io:format("[ps~w] unexpected message ~n",[Id]),
      ps_loop(Key,  N, Id)
  end,
  io:format("[ps~w] loop end ~n",[Id]).

ps_suspended(Key,N, Id) ->
  io:format("[ps~w] vote suspended ~n", [Id]),
  receive
    {_,resume_vote } ->
      io:format("[ps~w] vote restarted ~n", [Id]),
      ps_loop(Key,  N, Id);
    {_,stop_vote} -> ps_stopped();
    {Admin, get_turnout}->
      send_turnout(Admin),
      ps_suspended(Key,N, Id);
    {Admin, get_list}->
      send_list(Admin),
      ps_suspended(Key,N, Id);
    {Admin,get_status} ->Admin !{self(), "suspended"},
      ps_suspended(Key,N, Id);
    _ ->  ps_suspended(Key,N, Id)
  end.

ps_stopped() ->
  io:format("[ps ] vote stopped ~n"),
  receive
    {Admin, get_turnout}->
      send_turnout(Admin),
      ps_stopped();
    {Admin, get_list}->
      send_list(Admin),
      ps_stopped();
    {Sender,_} ->Sender !{self(), "stopped"},
      ps_stopped()
  end.
