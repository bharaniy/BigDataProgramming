import org.apache.spark.{ SparkConf, SparkContext }

object SecondarySort {

  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir","C:\\winutils" )
    val conf = new SparkConf().setAppName("SECONDARYSORTING").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val personRDD = sc.textFile("input/input2.txt")
    val pairsRDD = personRDD.map(_.split(",")).map { m => ((m(0), m(1)),m(2))}
    println("PAIRS")
    pairsRDD.foreach { println }
    val numReducers = 4;

    val listRDD = pairsRDD.groupByKey(numReducers).mapValues(iter => iter.toList.sortBy(k => k))
    println("LIST")

    listRDD.saveAsTextFile("data/output2");

  }
}
