%%%-------------------------------------------------------------------
%%% @author yuri
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 13. lug 2022 10:58
%%%-------------------------------------------------------------------
-module('monitor').
-author("yuri").
-compile(import_all).
%% API
-export([startServices/0, start_link/0, init/1]).

start_link() ->
  Args = [], %% TODO pass the host list as arg
  %% IMPORTANT: supervisor:start_link register globally the supervisor as "my_supervisor"
  {State, PidMonitor} = supervisor:start_link({global, ?MODULE}, ?MODULE, Args),
  io:format(" [MONITOR] Supervisor pid is ~p state is ~p~n", [PidMonitor, State]),
  PidMonitor.


%% The init callback function is called by the start_link
init(_Args) -> % TODO this is copy pasted from AuctionHandler!!
  io:format(" [MONITOR] Init function started ~n"),
  %% Configuration options common to all children.
  %% If a child process crashes, restart only that one (one_for_one).
  %% If there is more than 1 crash ('intensity') in
  %% 5 seconds ('period'), the entire supervisor crashes
  %% with all its children.
  SupFlags = #{strategy => one_for_one,
    intensity => 4,
    period => 5},
  CentralStation = #{id => centralStation,
    start => {centralStation, start, []},
    restart => permanent},
  List=[ps@studente76, ps@studente77, ps@studente78],
  %List=['ps@studente76'],
  %List=['ps@172.18.0.76'],
  %% permanent means that this process is always restarted
  PollingStations = polling_stations(List),
  Children = PollingStations++[CentralStation],
  %% Return the supervisor flags and the child specifications
  %% to the 'supervisor' module.
  {ok, {SupFlags, Children}}.

polling_stations(List)->polling_stations([],List, 1).

% fill the polling_stations_list, assign nodes and IDs
polling_stations(Ch,[], _)->
  Ch;
%polling_stations(Ch, [Elem], N)->polling_stations(Ch, [Elem,[]], N);
polling_stations(Ch,List, N)->
  [Head|Tail]=List,
  PS=#{id => Head,
    start => {pollingStation, start, [Head, N]},
    restart => permanent},
  polling_stations(Ch++[PS], Tail, N+1).

startServices()->
  Pid=spawn(centralStation,start,[]),
  register(central_station_endpoint, Pid),
  PsPid=spawn(pollingStation,start,[Pid]),
  register(polling_station_endpoint, PsPid).




