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
%% API
-export([start/1, ps_loop/2, init/0]).




start(Node)->
  try %% TODO maybe use a local monitor instead
    Pid=spawn(Node, ?MODULE,init,[]),
    io:format(" ps loop started ~n"),
    {ok,Pid}
  catch
      Error  -> {error,Error}
  end.

init()->
  Key=util:read_key("../../resources/ps_keys/ps1_key.pem"),
  ok,SequenceNumber = crypto:strong_rand_bytes(4),
  register(polling_station_endpoint, self()),
  ps_loop(Key ,binary:decode_unsigned(SequenceNumber)).

ps_eval(Key, Booth,Payload, N)->
  % verify voter sing and set flag into database
  VoterKey = util:read_key("../../resources/v_keys/v1_key.pem"),
  {Vote,VoterID,VoterSign} =Payload,
  io:format(" voter id ~s ~n", [VoterID]),
  Ok= public_key:verify(Vote,sha256, VoterSign,VoterKey),
  if not Ok->
    Booth ! {self(), error};% nack
  true ->
    io:format(" vote certified ~n"),
    Term=term_to_binary({Vote, N}), % append sequence number
    Signature = public_key:sign(Term, sha256, Key),
    %Signature = crypto:sign(dss, sha256,Term, Key),
    {cs@server0} ! {self(), Signature, {Vote, N}}
  end.


ps_loop(Key, N) ->
  %%Term = io:get_chars("prompt ", 5),
  receive
    {_, ok} ->ps_loop(Key,  N);
    {Booth, Payload} -> ps_eval(Key,Booth,Payload, N+1),
      io:format(" ps sent ~n"),
      ps_loop(Key,  N+1);
    _ -> io:format("wathever ps ~n"),
      ps_loop(Key,  N)
  end,
  io:format(" ps end ~n").