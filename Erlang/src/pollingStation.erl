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

  ps_loop(Key, Cs).

ps_eval(Key,Cs, Term)->
  % TODO verify voter sing and set flag into database
  Signature = public_key:sign(Term, sha256, Key),
  %Signature = crypto:sign(dss, sha256,Term, Key),
  % TODO send acknowledge to sender node
  Cs ! {self(), Signature, Term}.

ps_loop(Key, Cs) ->
  %%Term = io:get_chars("prompt ", 5),
  receive
    {_, ok} ->ps_loop(Key, Cs);
    {_, Payload} -> ps_eval(Key,Cs,Payload),
      io:format(" ps sent ~n"),
      ps_loop(Key, Cs);
    _ -> io:format("wathever ps ~n"),
      ps_loop(Key, Cs)
  end,
  io:format(" ps end ~n").