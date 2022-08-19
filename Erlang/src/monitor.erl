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
-export([startServices/0]).

startServices()->
  Pid=spawn(centralStation,start,[]),
  register(central_station_endpoint, Pid),
  PsPid=spawn(pollingStation,start,[Pid]),
  register(polling_station_endpoint, PsPid).


