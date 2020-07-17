import os
from pyspark import SparkContext
from pyspark.streaming import StreamingContext


"""
This is use for create streaming of text from txt files that creating dynamically 
from files.py code. This spark streaming will execute in each 3 seconds and It'll
show number of words count from each files dynamically
"""

os.environ["SPARK_HOME"] = 'C:/spark-3.0.0-bin-hadoop2.7'
sc = SparkContext("local[2]", "NetworkWordCount")
logger = sc._jvm.org.apache.log4j
logger.LogManager.getLogger("org").setLevel( logger.Level.OFF )
logger.LogManager.getLogger("akka").setLevel( logger.Level.OFF )
ssc=StreamingContext(sc,3)
lines = ssc.textFileStream('C:/Users/bharani/PycharmProjects/sicp4/log')
words=lines.flatMap(lambda line:line.split(' '))
wordtup=words.map(lambda word:(word,1))
wordcount=wordtup.reduceByKey(lambda x,y:x+y)
wordcount.pprint()
ssc.start()
ssc.awaitTermination()

