### GET TASK WITH ID ###################################################################################################

#!/bin/bash

# Base URL
base_url="http://localhost:8080/tasks"

# Loop from 1 to 1000
for i in {1..1000}
do
  # Construct the full URL
  url="${base_url}/${i}"

  # Perform the GET request with curl
  curl "$url"
done

########################################################################################################################