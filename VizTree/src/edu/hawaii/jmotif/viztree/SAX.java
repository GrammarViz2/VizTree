package edu.hawaii.jmotif.viztree;

import java.util.ArrayList;
import java.util.Arrays;

public class SAX {
      private int      num_seg;
      private byte     alphabet_size;
      private double[]   time_series;
      private double[]   breakpoints;
      private byte[]     symbols;
      private double[][]  dist_matrix;

      // constructor
      public SAX(int nseg, byte asize)
      {

        time_series = new double[0];

        try
        {
          if (asize < 2 || asize > 10)
          {
            throw new Exception();          
          }
        }

        catch (Exception e)
        {
          System.err.println("Alphabet size must be between 2 and 10!");
          System.err.println("Re-assign alphabet size to 4");
          
          asize = 4;

          //Console.In.ReadLine();
        }

        finally
        {
          num_seg     = nseg;
          alphabet_size = asize;
          symbols = new byte[num_seg];
          
          switch (alphabet_size)
          {
            case 2:
              breakpoints = new double[] {0};
              break;

            case 3:
              breakpoints = new double[] {-0.43, 0.43};
              break;

            case 4:
              breakpoints = new double[] {-0.67, 0, 0.67};
              break;

            case 5:
              breakpoints = new double[] {-0.84, -0.25, 0.25, 0.84};
              break;

            case 6:
              breakpoints = new double[] {-0.97, -0.43, 0, 0.43, 0.97};
              break;

            case 7:
              breakpoints = new double[] {-1.07, -0.57, -0.18, 0.18, 0.57, 
                               1.07};
              break;

            case 8:
              breakpoints = new double[] {-1.15, -0.67, -0.32, 0, 0.32, 
                               0.67, 1.15};
              break;

            case 9:
              breakpoints = new double[] {-1.22, -0.76, -0.43, -0.14, 0.14, 
                               0.43, 0.76, 1.22};
              break;

            case 10:
              breakpoints = new double[] {-1.28, -0.84, -0.52, -0.25, 0, 
                               0.25, 0.52, 0.84, 1.28};
              break;

              // This shouldn't happen since exception has already been handled
            default:
              break;
      
          }

          BuildDistMatrix();

        }
      }

      // properties
      public int getNumSeg()
      {
          return num_seg;
      }
      
      public void setNumSeg(int value)
        {
          num_seg = value;
        }

      public byte getAlphabetSize()
      {
          return alphabet_size;
        }

      public void setAlphabetSize(int value)
        {
          alphabet_size = (byte) value;
        }
      

      public byte[] getSAX_symbol()
      {
          return symbols;
      }

      public void SetData(double[] data)
      {
        time_series = Arrays.copyOf(data,data.length);
      }

      public byte[] ConvertSAX(boolean NORM)
      {
        double rem = Math.IEEEremainder(time_series.length, num_seg);
        double[] PAA = new double[num_seg];

//        min = 0;
//        max = 0;

        // If the num_seg is not divisible by the length of time series, then 
        // find their GCD (greatest common divisor) and duplicate the time
        // series by this much (one number at a time).
        if (rem != 0)
        {
          int lcm = GetLCM();

          double[] ts_dup;// = new double[lcm];

          if (NORM)
          {
            // Normalize the time series first
            double[] normalized;// = new double[time_series.length];
            normalized = Normalize();

//            max = GetMax(normalized);
//            min = GetMin(normalized);

            // replicate the time series so that it can be divided into num_seg
            // evenly
            ts_dup = DupArray(normalized, lcm/time_series.length);
          }
          
          else
          {
            // replicate the time series so that it can be divided into num_seg
            // evenly
            ts_dup = DupArray(time_series, lcm/time_series.length);
          }
            
          PAA = GetPAA(ts_dup);

        }

        // If the length of time series is divisible by the number of segments,
        // then no replication is needed.  We can work directly on the original
        // time series
        else
        {
          if (NORM)
          {
            // Normalize the time series first
            double[] normalized;// = new double[time_series.length];
            normalized = Normalize();

//            max = GetMax(normalized);
//            min = GetMin(normalized);

            PAA = GetPAA(normalized);
          }
          
          else
          {
          //  max = GetMax(normalized);
          //  min = GetMin(normalized);

            PAA = GetPAA(time_series);
          }
          
        }

        //  Console.WriteLine("PAA Segments:");
        //  for (int i = 0; i < PAA.length; i++)
        //    Console.WriteLine(Convert.ToString(PAA[i]));

        //  byte[] symbols = new byte[num_seg];
        symbols = GetSymbol(PAA);

        return symbols;
      }
      
      public byte[] GetSymbol(double[] PAA)
      {     
        boolean FOUND = false;

        for (int i = 0; i < num_seg; i++)
        {
          for (int j = 0; j < alphabet_size - 1; j++)
          {
            if (PAA[i] <= breakpoints[j])
            {
              symbols[i] = (byte)(alphabet_size - j);
          //    symbols[i] = (byte)(j + 1);
              FOUND = true;
              break;
            }
          }

          if (!FOUND)
          {
//            symbols[i] = (byte)alphabet_size;
            symbols[i] = 1;
          }

          FOUND = false;
        }

        return symbols;

      }

      // 
      private double[] GetPAA(double[] data)
      {     
        // Determine the segment size
        int segment_size = data.length / num_seg;

        int offset = 0;

        double[] PAA = new double[num_seg];

        // if no dimensionality reduction, then just copy the data
        if (num_seg == data.length)
        {
          PAA = Arrays.copyOf(data, data.length); //PAA = data;
        }

        for (int i = 0; i < num_seg; i++)
        {
          PAA[i] = Mean(data, offset, offset + (int)segment_size - 1);
          offset = offset + (int)segment_size;
        }

        return PAA;
      }

      public double MinDist(byte[] data1, byte[] data2)
      {
        double min_dist = 0.0;

        if (data1.length== data2.length&& data1.length== num_seg)
        {
          for (byte i = 0; i < num_seg; i++)
          {
            min_dist = min_dist + dist_matrix[data1[i]-1][data2[i]-1];    
          }

          // Multiply min_dist by the compression ratio and take the square-root
          min_dist = Math.sqrt(time_series.length/num_seg * min_dist);

          return min_dist;
        }

        else
          return -1;
      }

      private void BuildDistMatrix()
      {
      
        dist_matrix = new double[alphabet_size][alphabet_size];

        for (byte i = 0; i < alphabet_size; i++)
        {
          for (byte j = i; j < alphabet_size; j++)
          {
            // Min_dist of the adjacent symbols is 0
            if (j <= i+1)
              dist_matrix[i][j] = 0;

            else
              // Sqaure the distance now for future use
              dist_matrix[i][j] = Math.pow((breakpoints[i] - breakpoints[j-1]),2);

            // The distance matrix is symmetric
            if (i != j)
              dist_matrix[j][i] = dist_matrix[i][j];
          }
        }
      }

      // Here we're doing a simplified version of mindist. Since we're only interested
      // in knowing if the distance of two strings is 0, we can do so without any extra
      // computation.  Since only adjacent symbols have distance 0, all we have to
      // do is check if any two symbols are non-adjacent.
      public boolean ZeroDist(byte[] data1, byte[] data2)
      {
        if (data1.length == data2.length && data1.length == num_seg)
        {
          for (byte i = 0; i < num_seg; i++)
          {
            // If any two symbols in the same offsets are differed by more than 1, then they're
            // not the same nor adjacent
            if (Math.abs(data1[i] - data2[i]) > 1)
            {
              return false;
            }
          }

          return true;
        }

        return true;
      }

      public boolean IsMonotonic(byte[] data)
      {
        byte ZERO = 0;
        byte POS = 1;
        byte NEG = 2;

        byte cur_sign;
        byte prev_sign;

        if (data.length == num_seg)
        {
          if ((data[1] - data[0]) > 0)
            prev_sign = POS;
          else if ((data[1] - data[0] < 0))
            prev_sign = NEG;
          else
            prev_sign = ZERO;

          for (byte i = 2; i < num_seg; i++)
          {
            if ((data[i] - data[i-1]) > 0)
              cur_sign = POS;
            else if ((data[i] - data[i-1]) < 0)
              cur_sign = NEG;
            else cur_sign = ZERO;

            if (cur_sign != prev_sign && cur_sign != ZERO && prev_sign != ZERO)
              return false;

            prev_sign = cur_sign;
          }
          return true;
        }

        return true;
      }
            
                      

      // Get the GCD (greatest common divisor) between the
      // length of the time series and the number of PAA
      // segments
      protected int GetGCD()
      {
        int u = time_series.length;
        int v = num_seg;
        int div;
        int divisible_check;

        while (v > 0)
        {
          div = (int)Math.floor((double)u/(double)v);
          divisible_check = u - v * div;
          u = v;
          v = divisible_check;
        }
        
        return u;
      }

      // Get the least common multiple of the length of the time series and the
      // number of segments
      protected int GetLCM()
      {
        int gcd = GetGCD();

        int len = time_series.length;
        int n = num_seg;

        return (len * (n / gcd));
      }

      // Make dup copies of each array element (one at a time)
      protected double[] DupArray(double[] data, int dup)
      {
        int cur_index = 0;
        double[] dup_array = new double[data.length * dup];

        for (int i = 0; i < data.length; i++)
        {
          for (int j = 0; j < dup; j++)
          {
            dup_array[cur_index+j] = data[i];
          }

          cur_index += dup;
        }

        return dup_array;
      }

      // Normalize the time series so it has mean 0 and standard deviation 1
      public double[] Normalize()
      {
        double mean = Mean();
        double std = StdDev();

        double[] normalized = new double[time_series.length];

        if (std == 0)
          std = 1;

        for (int i = 0; i < time_series.length; i++)
        {
          normalized[i] = (time_series[i] - mean) / std;
        }

        return normalized;
      }

      // Compute the standard deviation of the time series
      public double StdDev()
      {
        double mean = Mean();
        double var = 0.0;

        for (int i = 0; i < time_series.length; i++)
        {
          var += Math.pow((time_series[i] - mean), 2);
        }
        
        var /= (time_series.length - 1);

        return Math.sqrt(var);
      }

      // Calculate the average of any section of the data
      public double Mean(double[] data, int index1, int index2)
      {
        //try
        //{
        if (index1 < 0 || index2 < 0 || index1 >= data.length ||
          index2 >= data.length)
        {
          throw new Exception("Invalid index!");
        }
        //}
        
        if (index1 > index2)
        {
          int temp = index2;
          index2 = index1;
          index1 = temp;
        }

        double sum = 0;

        for (int i = index1; i <= index2; i++)
        {
          sum += data[i];
        }

        return sum / (index2 - index1 + 1);
      }

      public double Mean()
      {
        return Mean(time_series, 0, time_series.length-1);
      }

      private double GetMax(double[] data)
      {
        double max = data[0];

        for (int i = 1; i < data.length; i++)
        {
          if (data[i] > max)
            max = data[i];
        }

        return max;
      }

      private double GetMin(double[] data)
      {
        double min = data[0];

        for (int i = 1; i < data.length; i++)
        {
          if (data[i] < min)
            min = data[i];
        }
        return min;

      }

      
    }

    class SuffixTree
    {
      private int window_len;
      byte[][] arSymbol;
      int[] arOffset;
        
      public SuffixTree()
      {
        window_len = 0;
      }

      // Constructor
      public SuffixTree(int wlen)
      {
        window_len = wlen;
      }

      // Property
      public int WindowLen
      {
        get
        {
          return window_len;
        }

        set
        {
          window_len = value;
        }
      }

      public ArrayList LoadFile(String filename)
      {
        
        ArrayList data = new ArrayList();
        //      double[]  data    = new double[0];
   
        if (!File.Exists(filename)) 
        {
          Console.WriteLine("{0} does not exist!", filename);
          //MessageBox.Show("Error", "File does not exist",
          //  MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
    
          return data;
          //        return data; //("{0} does not exist!", FILE_NAME);
        }
   
        StreamReader sr = File.OpenText(filename);
        String input;
     
        //    try
        //    {
        string[] word = null;

      //  string delimStr = " ,.:\n";
      //  char [] delimiter = delimStr.ToCharArray();

        while ((input=sr.ReadLine())!=null) 
        {
          word = input.Split(' ');

          for (int i = 0; i < word.length; i++)
          {
            if (word[i] == "NaN")
            {
              data.Add(Double.NaN);
            }
            else
            {
              if (word[i] != "")
                data.Add((double) Double.Parse(word[i]));
            }
          }
        }
        
        Console.WriteLine(Convert.ToString(data.Count));
        sr.Close();

        CheckMissingValue(ref data);
     
        // Transfer file to data
        //      data = new double[numbers.Count]; // resize data
        //time_series = new double[numbers.Count];

        //      numbers.CopyTo(0, data, 0, numbers.Count); // copy to data
        //numbers.CopyTo(0, time_series, 0, numbers.Count);
      
    
        return data;
      }  

      // Do simple linear interpolation for missing values
      public void CheckMissingValue(ref ArrayList data)
      {
        // if no missing value is found, then return

        int start = 0;
        int end = 0;

        start = data.IndexOf(Double.NaN);

        if (start < 0)
          return;

        ArrayList missing = new ArrayList();

        do
        {
//          i = data.IndexOf(Double.NaN, j);
   
          // check for missing values
//          if (Double.IsNaN((double)data[i]))
//          {

            end = start;
            // = i;

            // get the last value before the missing value(s)
            if (start > 0)
            {
              missing.Clear();

              missing.Add((double)data[start-1]);
            
              while (end < data.Count && Double.IsNaN((double)data[end]))
              {
                missing.Add((double)data[end]);
                end++;
              }

              if (end < data.Count)
              {
                missing.Add((double)data[end]);

                InterpolateMissingValue(ref missing);

                for (int k = start; k < end; k++)
                  data[k] = missing[k-start+1];
              }

              // if this chunk is at the end of the time series, then just drop them
              else
              {
                data.RemoveRange(start, end-start);
                return;
              }
            }

            // if the missing value is in the beginning of the time series then just drop them
            else if (start == 0)
            {
              end = 0;

              while (end < data.Count && Double.IsNaN((double)data[end]))
                end++;
              
              data.RemoveRange(0, end);
            }

            // check if there is more missing value
        //    if (!data.Contains(Double.NaN))
        //      return;
    //      }

        } while ( (start = data.IndexOf(Double.NaN)) > 0 );
            
      }

      public void InterpolateMissingValue(ref ArrayList missing)
      {
        // simple case: if the first and last values are the same, then all the values in between are the same too
        if ( Math.Abs(((double)missing[0] - (double)missing[missing.Count-1])) < 0.00001 )
        {
          for (int i = 1; i < missing.Count - 1; i++)
            missing[i] = missing[0];
        }

        // linear interpolation
        else
        {
          double slope = ((double)(missing[missing.Count-1]) - (double)(missing[0])) / (missing.Count - 1);

          for (int i = 1; i < missing.Count - 1; i++)
            missing[i] = slope * i + (double)missing[0];
        }
      }

      // NR_option: numerosity reduction option
      //  1: record everything
      //  2: record string only if it's different from the previously recorded one
      //  3: record string only if its mindist from the previously recorded one > 0
      //  4: record string only if it's not monotonic
      public ArrayList BuildMatrix(ArrayList alData, ref int[] arOffset, byte alphabet_size, 
        int num_seg, boolean NORM, byte NR_option)
      {
        SAX s = new SAX(num_seg,alphabet_size);

        ArrayList alSymbol = new ArrayList();
        
        ArrayList alOffset = new ArrayList();
        
        byte[]   cur_string = new byte[num_seg];
        double[] cur_window = new double[window_len];
        byte[]   last_string = new byte[num_seg];
        int      subseq_count = 0;

//        double   cur_max = 0.0;
//        double   cur_min = 0.0;

//        min_norm = 0;
//        max_norm = 0;

      //  double[] normalized = new double[window_len];

        for (int i = 0; i < alData.Count - window_len + 1; i++)
        {
//          if (i % 200 > 0)
//            continue;
          
          // Get the current window (subsequence)
          alData.CopyTo(i,cur_window,0,window_len);

          
          // Set the data in the SAX class object
          s.SetData(cur_window);
          
          cur_string = s.ConvertSAX(NORM);

          byte[] string_copy = new byte[num_seg];

          Array.Copy(cur_string, 0, string_copy, 0, cur_string.length);

          // Record the first string no matter what (unless if we want only non-monotonic strings)
          if (i == 0)// && (NR_option != 4 || !s.IsMonotonic(string_copy)))
          {
            Array.Copy(cur_string, 0, last_string, 0, cur_string.length);
            alSymbol.Add(string_copy);
            alOffset.Add(i);
            subseq_count++;

//            max_norm = cur_max;
//            min_norm = cur_min;
//            max_norm = GetMax(cur_window);
//            min_norm = GetMin(cur_window);
          }

          else
          {
            // Depending on the numerosity reduction option chosen, determine if the
            // string should be recorded
            if ((NR_option == 1) ||
              (NR_option == 2 && !IsEqual(string_copy, last_string)) ||
              (NR_option == 3 && !s.ZeroDist(string_copy, last_string)))
            {
              alSymbol.Add(string_copy);        
              alOffset.Add(i);
              Array.Copy(string_copy, 0, last_string, 0, string_copy.length);
              subseq_count++;

  /*            if ( cur_max > max_norm )
                max_norm = cur_max;

              if ( cur_min < min_norm )
                min_norm = cur_min;*/
            }

            else if (NR_option == 4)
            {
              if (!s.IsMonotonic(string_copy))
              {
                if (alSymbol.Count == 0 || (alSymbol.Count > 0 && 
                  !s.ZeroDist(string_copy, last_string)))
                {
                  alSymbol.Add(string_copy);        
                  alOffset.Add(i);
                  Array.Copy(string_copy, 0, last_string, 0, string_copy.length);
                  subseq_count++;

  /*                if ( cur_max > max_norm )
                    max_norm = cur_max;

                  if ( cur_min < min_norm )
                    min_norm = cur_min;*/
                }
              } 
            }

            else if (NR_option == 5)
            {
              if ( (i % window_len) == 0 )
              {
                alSymbol.Add(string_copy);        
                alOffset.Add(i);
                Array.Copy(string_copy, 0, last_string, 0, string_copy.length);
                subseq_count++;

  /*              if ( cur_max > max_norm )
                  max_norm = cur_max;

                if ( cur_min < min_norm )
                  min_norm = cur_min;*/
              }
            }
          }
        }

        arSymbol = new byte[subseq_count,num_seg];
        arOffset = new int[subseq_count];

        // copy the offsets to an array
        alOffset.CopyTo(arOffset);

        // copy the arraylist of symbols into a two-dimensional array
        byte[] temp;

        for (int i = 0; i < subseq_count; i++)
        {
          temp = (byte[])alSymbol[i];
          
          for (int j = 0; j < num_seg; j++)
          {
            arSymbol[i,j] = temp[j];
          }
        }
      
        //      return arSymbol;
        return alSymbol;
      }

      private double GetMax(double[] data)
      {
        double max = data[0];

        for (int i = 1; i < data.length; i++)
        {
          if (data[i] > max)
            max = data[i];
        }

        return max;
      }

      private double GetMin(double[] data)
      {
        double min = data[0];

        for (int i = 1; i < data.length; i++)
        {
          if (data[i] < min)
            min = data[i];
        }
        return min;

      }

      public boolean IsEqual(byte[] data1, byte[] data2)
      {
        if (data1.length != data2.length)
          return false;

        boolean eq = true;

        for (int i = 0; i < data1.length; i++)
        {
          if (data1[i] != data2[i])
          {
            eq = false;
            break;
          }
        }

        return eq;
      }

    }   

    // Main
  /*  class Start
    {
      const string FILE_NAME = "5.dat";

      public static void Main(String[] args) 
      {
        byte alphabet_size = 4;
        int window_len = 32;
        int num_seg = 4;

        SuffixTree x = new SuffixTree(window_len);

        ArrayList alData = new ArrayList();

        alData = x.LoadFile("5.dat");
      
        byte[,] arSymbol;

        arSymbol = x.BuildMatrix(alData, alphabet_size, num_seg);

        for (int i = 0; i < arSymbol.GetLength(0); i++)
        {
          for (int j = 0; j < num_seg; j++)
          {
            Console.Write(Convert.ToString(arSymbol[i,j]));
          }
          Console.Write("\n");
        }

        Console.In.ReadLine();
      }
    }*/
  }
}
