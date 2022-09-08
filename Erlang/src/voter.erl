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
-export([init/0, insert_voter/1, set_voter_flag/1, get_voter_pub_key_from_id/1,
  test_insert/0, select_all_who_voted/0, voter_has_voted/1, delete_voter/1]).

%% https://www.erlang.org/doc/man/qlc.html
%% QLC = Query List Comprehensions
%% Note: using QLC is more expensive than using mnesia built-in functions
-include_lib("stdlib/include/qlc.hrl").
-include_lib("stdlib/include/ms_transform.hrl").
-include("voter.hrl").

%% 1> mnesia:create_schema([node()]).
%% 2> mnesia:start().
%% 3> voter:init().
%% 4> mnesia:info().
%%
init() ->
  %TODO pass database folder here and not from CLI
  application:set_env(mnesia, dir, "voter"),
  mnesia:create_schema([node()]),
  mnesia:start(),
  %TODO check if table already exists
  CreateTable = mnesia:create_table(voter,
                      [
                        {attributes, record_info(fields, voter)},
                        % TODO check if this is the proper way to pass current node
                        {disc_copies, [node()]}
                      ]
  ),
  case CreateTable of
    {aborted,{already_exists,voter}} -> table_already_exists;
    {atomic,ok} -> {atomic,ok}
  end.

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
      _ = '_'
    },
    mnesia:select(voter, [{HasVoted, [], ['$_']}])
  end,
  {atomic,Turnout}=mnesia:transaction(SelectAllWhoVoted),
  Turnout.

%% Get the public key of a voter given their id
get_voter_pub_key_from_id(Voter_id) ->
  SelectVoterPubKey = fun() ->
    Voter = #voter{voter_id = Voter_id,
      pub_key = '$1',
      _ = '_'
    },
    mnesia:select(voter, [{Voter, [], ['$1']}])
                       end,
  {atomic,[Key]}=mnesia:transaction(SelectVoterPubKey),
  Key.

% Check if a voter with given ID has already cast their vote
voter_has_voted(Voter_id) -> %TODO check if this works
  CheckVoter = fun() ->
    VoterHasVoted = #voter{voter_id = Voter_id,
        has_voted = '$1',
        _  =  '_'
      },
    mnesia:select(voter, [{VoterHasVoted, [], ['$1']}])
                  end,
  {atomic,[HasVoted]}=mnesia:transaction(CheckVoter),
  HasVoted.


% remove a voter from the db given their ID
delete_voter(Voter_id) ->
  % syntax of following line:   read(table, key, type of lock)
  DeleteVoter = fun () ->
        case mnesia:read(voter, Voter_id, write) of
          [Voter] ->
            mnesia:delete({voter, Voter_id}),
            {ok, Voter};
          [] ->
            mnesia:abort(not_exist)
        end
      end,
  mnesia:transaction(DeleteVoter).


test_insert() ->
  Vot1 = #voter{ voter_id = 2,
    name = "Mario",
    surname = "Rossi",
    dob = "1970-01-01",
    pub_key = "v2_public.pem",
    has_voted = false},
  Vot2 = #voter{ voter_id = 3,
    name = "Luigi",
    surname = "Verdi",
    dob = "1970-01-02",
    pub_key = "v3_public.pem",
    has_voted = false},
  Vot3 = #voter{ voter_id = 4,
    name = "Wario",
    surname = "Gialli",
    dob = "1970-01-03",
    pub_key = "v4_public.pem",
    has_voted = false},
  insert_voter(Vot1),
  insert_voter(Vot2),
  insert_voter(Vot3).
