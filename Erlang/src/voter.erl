%%%-------------------------------------------------------------------
%%% @author marco
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 04. set 2022 17:46
%%%-------------------------------------------------------------------
-module(voter).
-author("marco").

%% API
-export([init/0, insert_voter/1, set_voter_flag/1, get_voter_pub_key_from_id/1, test_insert/0, select_all_who_voted/0]).

%% https://www.erlang.org/doc/man/qlc.html
%% QLC = Query List Comprehensions
%% Note: using QLC is more expensive than using mnesia built-in functions
-include_lib("stdlib/include/qlc.hrl").
-include_lib("stdlib/include/ms_transform.hrl").
-include("voter.hrl").

%% 1> mnesia:create_schema([node()]).
%% 2> mnesia:start().
%% 3> company:init().
%% 4> mnesia:info().
%%
init() ->
  mnesia:create_table(voter,
                      [{attributes, record_info(fields, voter)}]).

insert_voter(Vot) ->

  % Create a Functional Object Fun and...
  InsertVoter = fun() ->
                mnesia:write(Vot)
        end,
  % ... pass it to the function mnesia:transaction().
  % Fun is run as a transaction, namely: it either fails or succeeds
  % Code manipulating the same data records can be run concurrently without inter-process
  % interference
  mnesia:transaction(InsertVoter).


%% Set the has_voted flag of a voter to true
set_voter_flag(Voter_id) ->
  % Create a Functional Object Fun
  Fun = fun() ->
                % syntax of following line:   read(table, key, type of lock)
                [V] = mnesia:read(voter, Voter_id, write),
                HasVoted = true,
                New = V#voter{has_voted = HasVoted},
                mnesia:write(New)
        end,
  mnesia:transaction(Fun).


%% return the list of all the voters with the has_voted flag set to true
select_all_who_voted() ->
  SelectAllWhoVoted = fun() ->
    HasVoted = #voter{ has_voted = true,
      name = '$1',
      surname = '$2',
      _ = '_'
    },
    mnesia:select(voter, [{HasVoted, [], ['$1', '$2']}])
  end,
  mnesia:transaction(SelectAllWhoVoted).

%% Get the public key of a voter given their id
get_voter_pub_key_from_id(Voter_id) ->
  SelectVoterPubKey = fun() ->
    Voter = #voter{voter_id = Voter_id,
      pub_key = '$1',
      _ = '_'
    },
    mnesia:select(voter, [{Voter, [], ['$1']}])
                       end,
  mnesia:transaction(SelectVoterPubKey).

test_insert() ->
  Vot1 = #voter{ voter_id = 2,
    name = "Mario",
    surname = "Rossi",
    dob = "1970-1-1",
    pub_key = asd,
    has_voted = false},
  Vot2 = #voter{ voter_id = 3,
    name = "Luigi",
    surname = "Verdi",
    dob = "1970-1-2",
    pub_key = qwe,
    has_voted = false},
  Vot3 = #voter{ voter_id = 4,
    name = "Wario",
    surname = "Gialli",
    dob = "1970-1-3",
    pub_key = zxc,
    has_voted = false},
  insert_voter(Vot1),
  insert_voter(Vot2),
  insert_voter(Vot3).
