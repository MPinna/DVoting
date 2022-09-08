cd /root/DVoting/Erlang/src
erl -mnesia dir "seggio" -sname cs@$(hostname) -setcookie "abcde" -kernel shell_history enabled
