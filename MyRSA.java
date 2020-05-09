// run and tested on linux

import java.util.Scanner;
import java.math.BigInteger;
import java.security.SecureRandom;

import java.io.*; // remove this

public class MyRSA {

  private BigInteger SK, PK;

  private BigInteger genRandomMRBase(BigInteger n) {
    BigInteger a;
    do {
      a = genRandomBigInt(n.bitLength());
    } while(a.compareTo(n) == -1); // a < n

    // ensuring a is positive
    if(a.remainder(new BigInteger("2")).intValue() == 1) {
      a.subtract(BigInteger.ONE);
    }

    return a;
  }

  // private boolean MRTest3(BigInteger n){
  //   BigInteger a = genRandomMRBase(n);
  //
  //   return false;
  // }

  private boolean MRWithBases(BigInteger n, BigInteger[] a) {
    // calculate S and d
    int S = 0;
    BigInteger d = n.subtract(BigInteger.ONE);
    do {
      d = d.divide(new BigInteger("2"));
      S++;
    } while(d.remainder(new BigInteger("2")).intValue() == 0); // while d is even

    BigInteger temp;
    BigInteger nMinusOne = n.subtract(BigInteger.ONE);

    loop: for(BigInteger base : a) {
      temp = FME(base, d, n);

      if( (temp.compareTo(BigInteger.ONE) == 0) || (temp.compareTo(nMinusOne) == 0) ) {
        continue;
      }

      for(int r = 1; r < S; r++) {
        temp = FME(base, d.multiply(BigInteger.valueOf(2).pow(r)), n);

        if(temp.compareTo(nMinusOne) == 0)
          continue loop;

      }

      return true; // composite
    }

    return false; // prime, maybe
  }

  private BigInteger genRandomBigInt(int bitLength) {
    return new BigInteger(bitLength, new SecureRandom());
  }
  //
  // private BigInteger genRandomBigPrime(int bitLength) {
  //   BigInteger temp = genRandomBigInt(bitLength);
  //   SecureRandom random = new SecureRandom();
  //
  //   do {
  //     temp = new BigInteger(bitLength, random);
  //   } while( MRTest(temp) );
  //
  //   return temp;
  // }

  private BigInteger FME(BigInteger A, BigInteger B, BigInteger C) {
    String binaryB = B.toString(2);
    BigInteger temp = BigInteger.ONE;

    for(int i = binaryB.length() - 1; i >= 0; i--) {
      A = A.mod(C);

      if(binaryB.charAt(i) == '1')
        temp = temp.multiply(A);

      A = A.pow(2);

    }

    return temp.mod(C);
  }

  private void keyGen(int bitLength) {
    // generate 2 random big prime ints
    BigInteger p = genRandomBigInt(bitLength);
    BigInteger q = genRandomBigInt(bitLength);

    // prime test


  }

  public static void main(String[] args) {
    // test
    MyRSA asd = new MyRSA();
    BigInteger a = BigInteger.valueOf(2).pow(35);
    BigInteger b = new BigInteger("561");

    System.out.println(asd.genRandomBigInt(3));

    // generate key

    // user input
    BigInteger c = a;
    c = c.add(BigInteger.ONE);
    System.out.println("a + b = " + a.add(b).toString());
    System.out.println("a = " + a.toString());
    System.out.println("c = " + c.toString());
    clearConsole();


    System.out.println("c = " + c.toString(2));
    String text=System.console().readLine(); 
    System.out.println( "mod: " + asd.FME( BigInteger.valueOf(7), BigInteger.valueOf(256), BigInteger.valueOf(13) ) );

    // encryption / decryption

  }

  public final static void clearConsole()
  {
    // System.out.print("\033[H\033[2J");
    // System.out.flush();

    // try
    // {
    //     final String os = System.getProperty("os.name");
    //
    //     if (os.contains("Windows"))
    //     {
    //         Runtime.getRuntime().exec("cls");
    //     }
    //     else
    //     {
    //         Runtime.getRuntime().exec("pwd");
    //         String[] s = new String[]{"ls"};
    //         Runtime.getRuntime().exec(s);
    //     }
    // }
    // catch (final Exception e)
    // {
    //     //  Handle any exceptions.
    // }
    Process process = null;
    try
            {
            process = Runtime.getRuntime().exec("clear"); // for Linux
            //Process process = Runtime.getRuntime().exec("cmd /c dir"); //for Windows

            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
               while ((line=reader.readLine())!=null)
               {
                System.out.println(line);
                }
             }
                catch(Exception e)
             {
                 System.out.println(e);
             }
             finally
             {
               process.destroy();
             }
  }

}
