%%%-------------------------------------------------------------------
%%% @author marco
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 30. ago 2022 08:53
%%%-------------------------------------------------------------------
-module(seggio).
-author("marco").

%% launch script with
%% erl -mnesia dir '"path/to/MnesiaDB"

%% API
-export([init/0, insert_seggio/1, get_seggio_pub_key_from_id/1, update_seggio_pub_key/2, test_insert/0, test_get_pub_key/0]).

%% https://www.erlang.org/doc/man/qlc.html
%% QLC = Query List Comprehensions
-include_lib("stdlib/include/qlc.hrl").
-include_lib("stdlib/include/ms_transform.hrl").
-include("seggio.hrl").

init() ->
  mnesia:start(),
  mnesia:create_table(seggio,
    [{attributes, record_info(fields, seggio)}]).


% insert a new polling station into the seggio db
insert_seggio(Seg) ->

  InsertSeggio = fun() ->
    mnesia:write(Seg)
                end,
  mnesia:transaction(InsertSeggio).

% retrieve the pubkey of a polling station given its id
get_seggio_pub_key_from_id(Seggio_id) ->
  SelectSeggioPubKey = fun() ->
    Seggio = #seggio{seggio_id = Seggio_id,
        pub_key = '$1',
        _ = '_'
      },
    mnesia:select(seggio, [{Seggio, [], ['$1']}])
    end,
  {atomic,[Key]}=mnesia:transaction(SelectSeggioPubKey),
  Key.

update_seggio_pub_key(Seggio_id, NewKey) ->
  UpdateSeggioPubKey = fun() ->
    [S] = mnesia:read(seggio, Seggio_id, write),
    PubKey = NewKey,
    New = S#seggio{pub_key = PubKey},
    mnesia:write(New)
    end,
  mnesia:transaction(UpdateSeggioPubKey).


test_insert() ->
  Seg1 = #seggio{seggio_id = 1,
    name = "Rosso",
    city = "Roma",
    address = "Via Milano, 1",
    pub_key = "../../resources/ps_keys/ps1_public.pem",
    phone = "333123123123"},
  Seg2 = #seggio{seggio_id = 2,
    name = "Blu",
    city = "Milano",
    address = "Via Torino, 1",
    pub_key = "../../resources/ps_keys/ps2_public.pem",
    phone = "333123123124"},
  Seg3 = #seggio{seggio_id = 3,
    name = "Giallo",
    city = "Torino",
    address = "Via Roma, 1",
    pub_key = "../../resources/ps_keys/ps3_public.pem",
    phone = "333123123125"},
  insert_seggio(Seg1),
  insert_seggio(Seg2),
  insert_seggio(Seg3).

test_get_pub_key() ->
  [get_seggio_pub_key_from_id(1),
  get_seggio_pub_key_from_id(2),
  get_seggio_pub_key_from_id(3)].