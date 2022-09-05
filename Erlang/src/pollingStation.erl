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
     io:format("ps loop started on pid ~p ~n", [Pid]),
    {ok,Pid}
  catch
      Error  -> {error,Error}
  end.

init(Id)->

  Key=util:read_key("../../resources/ps_key.pem"),
  ok,SequenceNumber = crypto:strong_rand_bytes(4),
  voter:init(),
  voter:test_insert(),
  %global:register_name(polling_station_endpoint, self()),
  % TODO check endpoints names names
  register(polling_station_endpoint, self()),
  ps_loop(Key ,binary:decode_unsigned(SequenceNumber), Id).

send_turnout(Admin)->
  Turnout=voter:select_all_who_voted(),
  Admin !{self(), Turnout},
  io:format("turnout sent ~p ~n",[Admin]).

ps_loop(Key, N, Id) ->
  %%Term = io:get_chars("prompt ", 5),
  receive
    {_, ok} ->ps_loop(Key,  N, Id);
    {_,suspend_vote}-> ps_suspended(Key,N, Id);
    {Admin,get_status} ->Admin !{self(), "open"},
      io:format("status sent ~p ~n",[Admin]),
      ps_loop(Key,  N, Id);
    {Admin, get_turnout}->
      send_turnout(Admin),
      ps_loop(Key,  N, Id);
    {_Booth, Payload, vote} -> try
      % TODO verify voter sing and set flag into database
      VoterKey = util:read_key("../../resources/v2_public.pem"),
      {Vote,VoterID,VoterSign} =Payload,
      io:format(" voter id ~s ~n", [VoterID]),
      true= public_key:verify(Vote,sha256, VoterSign,VoterKey),
      io:format(" vote certified ~n"),
      Term=term_to_binary({Vote, N}), % append sequence number
      Signature = public_key:sign(Term, sha256, Key),
      %Signature = crypto:sign(dss, sha256,Term, Key),
      {central_station_endpoint,cs@studente75} ! {Id, Signature, {Vote, N}},
      io:format(" ps sent ~n"),
      ps_loop(Key,  N+1, Id)
      catch _ ->ps_loop(Key,  N, Id) end;
    _ -> io:format("wathever ps ~n"),
      ps_loop(Key,  N, Id)
  end,
  io:format(" ps end ~n").

ps_suspended(Key,N, Id) ->
  io:format("vote suspended ~n"),
  receive
    {_,resume_vote } ->
      io:format("vote restarted ~n"),
      ps_loop(Key,  N, Id);
    {_,stop_vote} -> ps_stopped();
    {Admin, get_turnout}->
      send_turnout(Admin),
      ps_suspended(Key,N, Id);
    {Admin,get_status} ->Admin !{self(), "suspended"},
      ps_suspended(Key,N, Id);
    _ ->  ps_suspended(Key,N, Id)
  end.

ps_stopped() ->
  io:format("vote stopped ~n"),
  receive
    {Admin, get_turnout}->
      send_turnout(Admin),
      ps_stopped();
    {Sender,_} ->Sender !{self(), "stopped"},
      ps_stopped()
  end.