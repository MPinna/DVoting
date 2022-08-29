%%%-------------------------------------------------------------------
%%% @author yuri
%%% @copyright (C) 2022, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 27. ago 2022 14:38
%%%-------------------------------------------------------------------
-module(test).
-author("yuri").

%% API
-export([start/1]).

start(Node)->
  Pid = spawn(Node, echo() ),
  Pid !{self(), "eoeoeo"}.
echo()->
  receive
    Tuple-> io:format("~s ~n", [Tuple])
  end.