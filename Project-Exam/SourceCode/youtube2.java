package youtube;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class youtube2 {
	public static class Map extends Mapper<Object, Text, Text, FloatWritable> {		
		@Override
		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
			String[] row = value.toString().split("\t");
			if(row.length >= 7) {
				context.write(new Text(row[0]), new FloatWritable(Float.parseFloat(row[6])));
			}
		}
	}
	
	public static class Reduce extends Reducer<Text, FloatWritable, Text, FloatWritable> {
		private ArrayList<String[]> topTen = 
				new ArrayList<String[]>();
		
		@Override
		public void reduce(Text key, Iterable<FloatWritable> values, Context context) 
			throws IOException, InterruptedException {
				
			float rating = 0; 
			
			for(FloatWritable value : values) {
				rating = Float.parseFloat(value.toString());
			}
			
			if(topTen.size() < 10) {
				addToArrayList(topTen, key.toString(), rating);
			} else if(Float.parseFloat(topTen.get(9)[1]) 
					< rating) {
				topTen.remove(9);
				addToArrayList(topTen, key.toString(), rating);
			}
		}
		
		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {			
			for(String[] video : topTen) {
				context.write(new Text(video[0]), new FloatWritable(Float.parseFloat(video[1])));
			}
		}
		
		private void addToArrayList(ArrayList<String[]> array, 
				String key, float value) {
			int index = 0;
			while(index < array.size() && 
				Float.parseFloat(array.get(index)[1])
				> value) {
				++index;
			}
			String[] video = {key, String.valueOf(value)};
			array.add(index, video);
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
            System.err.println("Usage: Youtube <in_dir> <out_dir>");
            System.exit(2);
        }
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "youtube2");
		job.setJarByClass(youtube2.class);
		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FloatWritable.class);
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}