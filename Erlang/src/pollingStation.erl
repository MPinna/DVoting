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
%% API
%% API
-export([start/1]).

start(Cs)->
  {ok, PemBin} = file:read_file("../../resources/ps_keys/ps1_key.pem"),
  [DSAEntry] =  public_key:pem_decode(PemBin),
  Key = public_key:pem_entry_decode(DSAEntry),
  io:format(" ps loop started ~n"),
  SequenceNumber = crypto:strong_rand_bytes(4),
  ps_loop(Key, Cs, binary:decode_unsigned(SequenceNumber)).

ps_eval(Key,Cs, Booth,Payload, N)->
  % verify voter sing and set flag into database
  {ok, PemBin} = file:read_file("../../resources/v_keys/v1_key.pem"),
  [DSAEntry] =  public_key:pem_decode(PemBin),
  VoterKey = public_key:pem_entry_decode(DSAEntry),
  {Vote,VoterID,VoterSign} =Payload,
  Ok= public_key:verify(Vote,sha256, VoterSign,VoterKey),
  if not Ok->
    Booth ! {self(), error};% nack
  true ->
    io:format(" vote certified ~n"),
    Term=term_to_binary({Vote, N}), % append sequence number
    Signature = public_key:sign(Term, sha256, Key),
    %Signature = crypto:sign(dss, sha256,Term, Key),
    Cs ! {self(), Signature, {Vote, N}}
  end.


ps_loop(Key, Cs, N) ->
  %%Term = io:get_chars("prompt ", 5),
  receive
    {_, ok} ->ps_loop(Key, Cs, N);
    {Booth, Payload} -> ps_eval(Key,Cs,Booth,Payload, N+1),
      io:format(" ps sent ~n"),
      ps_loop(Key, Cs, N+1);
    _ -> io:format("wathever ps ~n"),
      ps_loop(Key, Cs, N)
  end,
  io:format(" ps end ~n").