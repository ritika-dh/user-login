Command for starting the application:-
1) move to the downloaded user-login folder and run mvn clean package -DskipTests
2) docker-compose up --build


commands for testing the application:-
Please read this file from code section in github instead of preview to get proper escape characters
1) try to access resource without signing in
 curl -X GET http://localhost:8080/ping -w "%{http_code}"

 output:- 401 

2) signup a new user
 curl -H "Content-Type:application/json" -X POST http://localhost:8080/signup -d "{\"userName\":\"abc\",\"password\":\"1234\"}" -w "%{http_code}"

 output:- 200 OK

3) try signing in with wrong password
 curl -H "Content-Type:application/json" -X POST http://localhost:8080/signin -d "{\"userName\"
:\"abc\",\"password\":\"123\"}" -w "%{http_code}"

 output:- 401


4) sign in with correct password, accessToken and refreshToken received in response. install python 3 if you do not already have it
 response=$(curl -H "Content-Type:application/json" -X POST http://localhost:8080/signin -d "{\"userName\":\"abc\",\"password\":\"1234\"}")

 token=$(echo ${response} | python3 -c 'import json,sys;obj=json.load(sys.stdin);print(obj["token"])')

 refreshToken=$(echo ${response} | python3 -c 'import json,sys;obj=json.load(sys.stdin);print(obj["refreshToken"])')


5) try accessing resource with access token
curl -X GET http://localhost:8080/ping -H "Accept: application/json" -H "Authorization: Bearer ${token}"

output:- Hello World!

6) access token expires after 1 minute, try accessing resource after 1 minute
curl -X GET http://localhost:8080/ping -H "Accept: application/json" -H "Authorization: Bearer ${token}"

output:- JWT expired at 2024-06-18T13:26:35Z. Current time: 2024-06-18T13:27:14Z, a difference of 39702 milliseconds.

7) get a new access token by sending a refreshToken request
 token=$(curl -H "Content-Type:application/json" -X POST http://localhost:8080/refreshtoken -d "${refreshToken}")
check ${token} to confirm access token is recieved

9) try accessing resource with new access token
 curl -X GET http://localhost:8080/ping -H "Accept: application/json" -H "Authorization: Bearer ${token}"

output:- Hello World!

9) revoke the access token 
curl -H "Content-Type:application/json" -X POST http://localhost:8080/revoketoken -d "${token}"

output:- revoked

10) try accessing resource with the revoked token
curl -X GET http://localhost:8080/ping -H "Accept: application/json" -H "Authorization: Bearer ${token}" -w "%{http_code}"
output:- Access token has expired. Please login again 401

11) try getting a new access token from refresh token, wait 3 minutes for refresh token to expire
curl -H "Content-Type:application/json" -X POST http://localhost:8080/refreshtoken -d "${refreshToken}" -w "%{http_code}"

output:- Refresh token has expired. Please login again 401
