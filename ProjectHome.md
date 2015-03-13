It is a general Java RDP client for Windows Terminal Services. It is built base on properJavaRDP, but with more feature:

. support window 2008 now (solve problem of “wrong modulus size! expected64+8got:264”)

. support rdp 5 and cache.

. support windows media player

. replaced AWT component with Swing component

[![](https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=zhoupeng%2ehust%40gmail%2ecom&lc=US&item_name=JAVA%20RDP%20client&no_note=0&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest) Donate to get your required feature

What is seamless RDP?  -- run remote application with local feel http://zhoupengylx.blogspot.com/2010/03/seamless-java-rdp-client.html

How to work through a gateway or proxy? (RDP over HTTP)

1. download uagServer.jar, set it up on your remote sever and listen on 8080 port

2. use http option to run the RDP client.

example: java -jar JavaRDP2.0-20101110.jar-u Administrator -p 111111  192.168.1.5:3389 --overHttp 192.168.1.5:8080/WSService/RDPSocket