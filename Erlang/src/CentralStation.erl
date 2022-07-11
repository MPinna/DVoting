%%%-------------------------------------------------------------------
%%% @author yuri
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 11. lug 2022 18:35
%%%-------------------------------------------------------------------
-module('CentralStation').
-author("yuri").

%% API
-export([]).

start(Key)->
  cs_loop(Key,"helloNode@localhost").


cs_loop(Key,Worker) ->
  receive
    {From, Msg_id, {vote, Sign,Payload} } ->
      Ok= crypto:verify(ecdsa,sha256,Payload, Sign,Key),
      if Ok==true -> Worker ! {self(), Payload};
        true-> io:format("\n Not valid sign \n\n")
        end,
      cs_loop(key, Worker);
    {From, Msg_id, {stop} } ->
      Worker ! {self(), stop},
      From ! {Msg_id, ok};
    _ ->
      cs_loop(Key,Worker)
  end.