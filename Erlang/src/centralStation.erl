%%%-------------------------------------------------------------------
%%% @author yuri
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 11. lug 2022 18:35
%%%-------------------------------------------------------------------
-module('centralStation').
-author("yuri").
-include_lib("public_key/include/public_key.hrl").
-include("util.hrl").
-include("seggio.hrl").
%% API
-export([start/0, cs_loop/2, init/0]).

start()->
  try
    Pid=spawn(?MODULE, init,[]),
    io:format("[cs ] loop started on pid ~p ~n", [Pid]),
    {ok,Pid}
  catch
      Error  -> {error,Error}
  end.

init()->
  PrvKey =util:read_key("cs_keys/cs_key.pem"),
  seggio:init(),
  seggio:test_insert(),
  register(central_station_endpoint, self()),
  cs_loop(PrvKey ,0).


cs_loop(PrvKey, N) ->
  receive
    {PS, Sign,Payload} -> try
        io:format("[cs ] received vote from ps~w \n", [PS]),
        KeyUrl=seggio:get_seggio_pub_key_from_id(PS),
        io:format("[cs ] key url: ~w \n", [KeyUrl]),
        PubKey = util:read_key("ps_keys/"++KeyUrl),
        Bin=term_to_binary(Payload),
        true= public_key:verify(Bin,sha256, Sign,PubKey),
        {Vote, SeqN}=Payload,
        if (SeqN>N) ->
          io:format("[cs ] valid sign \n"),
          {cs, central@localhost}! {self(), Vote},
          cs_loop(PrvKey,SeqN);
          true-> io:format("[cs ] valid sign: vote discarded \n"),
            cs_loop(PrvKey,N)
        end
        catch ErrorType:ErrorReason:Stacktrace ->
          io:format("[cs ] ERROR: ~w, ~w, ~w ~n", [ ErrorType,ErrorReason,Stacktrace]),
          cs_loop(PrvKey,N) end;
    {Sender,request_candidates} ->
      ok=send_candidates(Sender, PrvKey),
      cs_loop(PrvKey,N);
    {_, close_vote} ->cs_stopped(); % TODO check sender pid=={cs, central@localhost} pid or use signed message

    _ -> io:format("[cs ] unexpected message ~n"),
      cs_loop(PrvKey, N)
  end,
 io:format("[cs ] loop terminated \n").

send_candidates(Sender, Key) ->
  {ok, Msg} = file:read_file(util:basePath()++"candidates.txt"), % TODO verify if this works
  %%Msg=list_to_binary("Tizio 1_Tizio 2"),
  Signature = public_key:sign(Msg, sha256, Key),
  Sender! {self(), Signature, Msg},
  io:format("[cs ] candidates list sended ~p ~n", [Sender]),
  ok.

cs_stopped() ->
  {cs, central@localhost} ! {self(),vote_closed},
  io:format("[cs ] vote stoppped ~n"),
  receive
    {Sender,_} ->Sender !{self(), "stopped"},
      cs_stopped()
  end.
