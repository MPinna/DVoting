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
%% API
-export([start/0, test/0]).

start()->
  {ok, PemBin} = file:read_file("../../Resources/pubkey.pem"),
  [Entry] = public_key:pem_decode(PemBin),
  PublicKey=public_key:pem_entry_decode(Entry),
  cs_loop(PublicKey).

test()->
  io:format("\n TEST \n\n"),

  cs_loop(key).

cs_loop(Key) ->
  receive
    {_, Sign,Payload} ->
      io:format("\n received \n\n"),
      Ok= public_key:verify(Payload,sha256, Sign,Key),
      if Ok==true ->io:format("\n Valid sign \n\n");
        true-> io:format("\n Not valid sign \n\n")
        end,
      {javaMbox, javaNode@localhost}! {self(), Payload},
      cs_loop(Key);
    _ -> io:format("~n watherver ~n"),
      cs_loop(Key)
  end.