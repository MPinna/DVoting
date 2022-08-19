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
  %{ok, PemBin} = file:read_file("../../resources/ps_keys/ps1_cert.pem"),
  %[Entry] = public_key:pem_decode(PemBin),
  %{_, DerCert, _} = Entry,
  %Decoded=public_key:pkix_decode_cert(DerCert, otp),
  %PublicKey =
  %  Decoded#'OTPCertificate'.tbsCertificate#
  %    'OTPTBSCertificate'.subjectPublicKeyInfo#
  %      'OTPSubjectPublicKeyInfo'.subjectPublicKey,
  io:format("cs loop started ~n"),
  {ok, Bin} = file:read_file("../../resources/ps_keys/ps1_public.pem"),
  [DSAEntry] = public_key:pem_decode(Bin),
  Key = public_key:pem_entry_decode(DSAEntry),
  cs_loop(Key).

test()->
  io:format("\n TEST \n\n"),

  cs_loop(key).

cs_loop(Key) ->
  receive
    {_, Sign,Payload} ->
      io:format("\n received \n"),
      Ok= public_key:verify(Payload,sha256, Sign,Key),
      io:format(base64:encode_to_string(Sign)),
      %Ok= crypto:verify(dss,sha256, Payload,Sign,Key),
      if Ok==true ->io:format("\n Valid sign \n\n"),
        {cs, central@localhost}! {self(), Payload};
        true-> io:format("\n Not valid sign \n\n")
        end,

      cs_loop(Key);
    _ -> io:format("~n watherver cs ~n"),
      cs_loop(Key)
  end,
 io:format("\n end \n\n").