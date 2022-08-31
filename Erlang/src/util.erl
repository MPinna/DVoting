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
-export([read_key/1]).

read_key(Path)-> % TODO move to a utility module
  {ok, PemBin} = file:read_file(Path),
  [DSAEntry] =  public_key:pem_decode(PemBin),
  public_key:pem_entry_decode(DSAEntry).