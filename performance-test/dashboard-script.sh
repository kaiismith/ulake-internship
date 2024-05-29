HOST="http://dashboard.ulake.usth.edu.vn/api/file"
AUTH="eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3NvbnRnLm5ldC9pc3N1ZXIiLCJ1cG4iOiJ0aGFuZ2RkLmJpMTItNDA2QHN0LnVzdGguZWR1LnZuIiwiZ3JvdXBzIjpbIlVzZXIiLCJBZG1pbiJdLCJhdXRoX3RpbWUiOjE3MTY5NjQwMTM3NzAsInN1YiI6IjEiLCJpYXQiOjE3MTY5NjQwMTMsImV4cCI6MTcxNzA1MDQxMywianRpIjoiNzUzOTE1NTctYjhkNi00YmE0LTk3NjMtMDE0YjE1OGE0NzA3In0.WeD_jaJB2GMHNEenH7_hDxafmXxZUPVdqQSLhOaAr7gHmj5c6m4iRpFj_509IFedR6dLwV8IgoGxa0FPmMKdyVhcGSGytuc4NR9F76t8awd_RL9jrp0EUevpoyheCQ_jHdXZA4g0KAanmwAKDm3OrljZ3X-TNYKcPa7Pcr0q1wLZtDsxognGhipVrGQjmyNniCckXsMKLdHsjEIlI8dogxZ8AoP_krvGd994f0Y4Rwf43rKWH_AEKg1rgWxIqun85eAsYMbdHPVxqPI_hBXeNLpFjhwD0j1sMMl7JacVWwSPi8d8Ty-xwzbo5-g0Lg74teoBI-pn_CPiITO_rHlfjw"
#HOST="localhost:5000"

if [[ $1 != "" ]]; then
        AUTH=$1
fi

curl -v -X 'POST' \
  "$HOST" \
  -H "Authorization: Bearer $AUTH" \
  -H 'Content-Type: multipart/form-data' \
  -F 'fileInfo={ "mime": "text/plain", "name": "test_file", "ownerId": 2004, "size": 813050 };type=application/json' \
  -F 'file=/home/kaiismith/upload/test_file.txt;type=application/octet-stream'
