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
%% API
-export([start/0, cs_loop/2, init/0]).

start()->
  %{ok, PemBin} = file:read_file("../../resources/ps_keys/ps1_cert.pem"),
  %[Entry] = public_key:pem_decode(PemBin),
  %{_, DerCert, _} = Entry,
  %Decoded=public_key:pkix_decode_cert(DerCert, otp),
  %PublicKey =
  %  Decoded#'OTPCertificate'.tbsCertificate#
  %    'OTPTBSCertificate'.subjectPublicKeyInfo#
  %      'OTPSubjectPublicKeyInfo'.subjectPublicKey,
  try
    Pid=spawn(?MODULE, init,[]),
    io:format("cs loop started ~n"),
    {ok,Pid}
  catch
      Error  -> {error,Error}
  end.

init()->
  PrvKey =util:read_key("../../resources/cs_keys/cs_key.pem"),
  register(central_station_endpoint, self()),
  cs_loop(PrvKey ,0).

cs_loop(PrvKey, N) ->
  receive
    {PS, Sign,Payload} ->
      PubKey = util:read_key("../../resources/ps_keys/ps1_public.pem"),
      Ok= public_key:verify(term_to_binary(Payload),sha256, Sign,PubKey),
      {Vote, SeqN}=Payload,
      %io:format(base64:encode_to_string(Sign)),
      %Ok= crypto:verify(dss,sha256, Payload,Sign,Key),
      if Ok==true and (SeqN>N) ->
        io:format("ps valid sign \n"),
        {cs, central@localhost}! {self(), Vote},
        cs_loop(PrvKey,SeqN);
        true-> io:format("\n Not valid sign \n\n")
      end,
    cs_loop(PrvKey,N);
    {Sender,request_candidates} ->
      send_candidates(Sender, PrvKey),
      cs_loop(PrvKey,N);
    {central@localhost, close_vote} ->cs_stopped();
    _ -> io:format("~n watherver cs ~n"),
      cs_loop(PrvKey, N)
  end,
 io:format("\n end \n\n").

send_candidates(Sender, Key) ->
  Msg=list_to_binary("Tizio 1_Tizio 2"),
  Signature = public_key:sign(Msg, sha256, Key),
  Sender! {self(), Signature, Msg},
  io:format(" candidates list sended~n").

cs_stopped() ->
  receive
    {Sender,_} ->Sender !{self(), "stopped"},
      cs_stopped()
  end.
