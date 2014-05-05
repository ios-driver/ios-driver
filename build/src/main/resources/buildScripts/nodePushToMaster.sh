#!/bin/sh
# reports back the tests results back to the parent.
#echo $USER
#echo $PARENT_WORKSPACE
#echo $PARENT_JOB_NAME
#echo $MODULE
MAX_RETRIES=4
env
find . -name 'testng-results.xml'

# reports results to the parent project.
RETRIES=0
while [ $RETRIES -lt $MAX_RETRIES ]
do
	#find . -name 'testng-results.xml' -exec scp {} $USER@$MACHINE:$PARENT_WORKSPACE/$MODULE/target/testng-results-$NODE_NAME.xml \;
	rsync -vv $MODULE/target/surefire-reports/testng-results.xml $USER@$MACHINE:$PARENT_WORKSPACE/$MODULE/target/testng-results-$NODE_NAME.xml
        if [ $? -eq 0 ]
	then
	      echo "all good - results copied"
	      break
	else
	             RETRIES=$( expr $RETRIES + 1 )
                     echo "ERROR.Retrying later"
	             sleep 30
	fi
done

# reports back the metrics to the parent project.
RETRIES=0
while [ $RETRIES -lt $MAX_RETRIES ]
do
	find . -name '*.metrics' -exec scp {} $USER@$MACHINE:$PARENT_WORKSPACE/$MODULE/target/duration_per_class-$NODE_NAME-$PARENT_BUILD_NAME.metrics \;
	if [ $? -eq 0 ]
	then
	      echo "all good - metrics copied"
	      break
	else
	             RETRIES=$( expr $RETRIES + 1 )
	             sleep 30
	fi
done

