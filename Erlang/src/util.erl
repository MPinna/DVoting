%%%-------------------------------------------------------------------
%%% @author yuri
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 29. ago 2022 15:28
%%%-------------------------------------------------------------------
-module(util).
-author("yuri").

-include_lib("public_key/include/public_key.hrl").

%% API
-export([read_key/1, basePath/0]).

read_key(Path)->
  io:format("[utl] keyfile: ~s ~n",[basePath()++Path]),
  {ok, PemBin} = file:read_file(basePath()++Path),
  [DSAEntry] =  public_key:pem_decode(PemBin),
  public_key:pem_entry_decode(DSAEntry).

basePath()-> "../../resources/".
