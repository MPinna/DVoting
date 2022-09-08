cd /root/DVoting/Erlang/src
erl -mnesia dir "voter" -sname ps@$(hostname) -setcookie "abcde" -kernel shell_history enabled