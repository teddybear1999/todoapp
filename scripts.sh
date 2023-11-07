### GET TASK WITH ID ###################################################################################################
for i in {1..1000}; do curl "http://localhost:8080/tasks/$i"; sleep 0.5; done

### GET LIST OF COMPLETED TASKS ########################################################################################
for i in {1..100}; do curl -X GET "http://localhost:8080/tasks/completed"; sleep 5; done

### GET LIST OF UNCOMPLETED TASKS ########################################################################################
for i in {1..100}; do curl -X GET "http://localhost:8080/tasks/uncompleted"; sleep 5; done

### CREATE TASKS WITH POST METHOD#######################################################################################
for i in {1..10}; do
  month=$(printf "%02d" $((RANDOM % 12 + 1)))
  day=$(printf "%02d" $((RANDOM % 28 + 1)))
  random_date="2023-${month}-${day}"

  json_body="{\"description\":\"description${i}\",\"dueDate\":\"${random_date}\",\"isCompleted\":false}"

  curl -s -X POST "http://localhost:8080/tasks/" \
       -H "Content-Type: application/json" \
       -d "$json_body"
  sleep 10
done

### UPDATE STATUS TO TRUE ##############################################################################################
for i in {1..10}; do curl -s -X PUT "http://localhost:8080/tasks/${i}/completion?isCompleted=true"; sleep 1; done

### UPDATE STATUS TO FALSE #############################################################################################
for i in {1..1000}; do curl -s -X PUT "http://localhost:8080/tasks/${i}/completion?isCompleted=false"; sleep 1; done

### UPDATE TASK DESCRIPTION ############################################################################################
for i in {1..1000}; do json_body="description${i}"; curl -s -X PUT "http://localhost:8080/tasks/${i}/description" -H "Content-Type: application/json" -d "$json_body"; sleep 1; done

### DELETE TASKS #######################################################################################################
for i in {1..100}; do curl -s -X DELETE "http://localhost:8080/tasks/${i}"; sleep 1; done
