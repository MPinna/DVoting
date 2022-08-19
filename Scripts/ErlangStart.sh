. /home/yuri/kerl/erl24/activate
erlc ./*.erl
(echo "monitor:startServices()." ; cat)|erl \
-sname server@localhost \
-setcookie "abcde"