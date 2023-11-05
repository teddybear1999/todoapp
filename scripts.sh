### GET TASK WITH ID ###################################################################################################
for i in {1..1000}; do curl "http://localhost:8080/tasks/$i"; done

### GET LIST OF COMPLETED TASKS ########################################################################################
for i in {1..1000}; do curl -X GET "http://localhost:8080/tasks/completed"; done

### GET LIST OF UNCOMPLETED TASKS ######################################################################################
for i in {1..1000}; do curl -X GET "http://localhost:8080/tasks/uncompleted"; done

### CREATE TASKS WITH POST METHOD#######################################################################################
for i in {1..1000}; do
  # generate random date
  month=$(printf "%02d" $((RANDOM % 12 + 1)))
  day=$(printf "%02d" $((RANDOM % 28 + 1)))
  random_date="2023-${month}-${day}"

  # create json body
  json_body="{\"description\":\"description${i}\",\"dueDate\":\"${random_date}\",\"isCompleted\":false}"

  # sending put request
  curl -s -X POST "http://localhost:8080/tasks/" \
       -H "Content-Type: application/json" \
       -d "$json_body"
done

### UPDATE STATUS TO TRUE ##############################################################################################
for i in {1..1000}; do
  curl -s -X PUT "http://localhost:8080/tasks/${i}/completion?isCompleted=true"
done

### UPDATE STATUS TO FALSE #############################################################################################
for i in {1..1000}; do
  curl -s -X PUT "http://localhost:8080/tasks/${i}/completion?isCompleted=false"
done

### UPDATE TASK DESCRIPTION ############################################################################################
for i in {1..1000}; do
  json_body="description${i}"

  curl -s -X PUT "http://localhost:8080/tasks/${i}/description" \
       -H "Content-Type: application/json" \
       -d "$json_body"
done

### DELETE TASKS #######################################################################################################
for i in {1..100}; do
  curl -s -X DELETE "http://localhost:8080/tasks/${i}"
done
