#!/bin/bash
# http://stackoverflow.com/questions/8314499/read-n-lines-at-a-time-using-bash

# Loop through and read two lines at a time
cat SRAdump.txt | while read -r ONE; do
        read -r TWO
        read -r THREE
        read -r FOUR
        echo -e "$ONE \t $TWO \t $THREE \t $FOUR" > fastq.csv
done
