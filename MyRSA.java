// run and tested on linux

import java.util.Scanner;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import java.io.*; // remove this

public class MyRSA {

  private final int MIN_NUM_OF_BASES = 3;

  private BigInteger[] PK = new BigInteger[2];
  private BigInteger SK;
  private ArrayList<Integer> bases = new ArrayList<Integer>();

  private void setBasesForMR(ArrayList<Integer> a) {
    bases = a;
  }

  private int randomIntLessThan(BigInteger limit, boolean makeOdd) {
    int x;
    SecureRandom sr = new SecureRandom();

    do {
      x = sr.nextInt(limit.bitLength());
      if(makeOdd && (x % 2 == 0))
        x++;
    } while( (BigInteger.valueOf(x).compareTo(limit) == 1) || (x < 2) ); // 1 < x < limit

    System.out.println("x = " + x);
    System.out.println("limit = " + limit.toString());

    return x;
  }

  // private int generateE(BigInteger fiN) {
  //   int e;
  //   SecureRandom sr = new SecureRandom();
  //
  //   do {
  //     e = sr.nextInt();
  //     if(e % 2 == 1)
  //       e++;
  //   } while( (BigInteger.valueOf(e).compareTo(fiN) == -1) && (e > 1) ); // 1 < e < fi(n)
  //
  //   return e;
  // }

  private BigInteger genRandomBigIntLessThan(BigInteger limit) {
    BigInteger x;
    SecureRandom sr = new SecureRandom();
    do{
      x =  new BigInteger(limit.bitLength(), sr);
    } while((x.compareTo(limit) == 1) || (x.compareTo(BigInteger.valueOf(2)) == -1)); // 1 < x < limit

    return x;
  }

  private BigInteger genRandomBigPrime(int bitLength) {
    BigInteger x;
    SecureRandom random = new SecureRandom();

    do {
      x = new BigInteger(bitLength, random);

      //for performance
      if(x.remainder(new BigInteger("2")).intValue() == 0)
        x = x.add(BigInteger.ONE);

    } while( MRTest(x) );

    return x;
  }

    //this is useless now
  // private BigInteger genRandomMRBase(BigInteger n) {
  //   BigInteger a = genRandomBigIntLessThan(n);
  //   // do {
  //   //   a = genRandomBigInt(n.bitLength());
  //   // } while((a.compareTo(n) == -1) && (a.compareTo(BigInteger.ZERO) == 1)); // 0 < a < n
  //
  //   // ensuring a is (idk what this supposed to be)
  //   // if(a.remainder(new BigInteger("2")).intValue() == 1) {
  //   //   a.subtract(BigInteger.ONE);
  //   // }
  //
  //   return a;
  // }

  private boolean MRTest(BigInteger n){
    //BigInteger a = genRandomMRBase(n);

    if(n.compareTo(BigInteger.ZERO) == -1){
      System.out.println("MR test number can't be negative");
      return true;
    }

    for(int i = MIN_NUM_OF_BASES - bases.size(); i > 0; i--) {
      bases.add(randomIntLessThan(n, false));
    }

    return MRWithBases(n, bases);
  }

  private boolean MRWithBases(BigInteger n, ArrayList<Integer> a) {
    // calculate S and d
    int S = 0;
    BigInteger d = n.subtract(BigInteger.ONE);
    do {
      d = d.divide(new BigInteger("2"));
      S++;
    } while(d.remainder(new BigInteger("2")).intValue() == 0); // while d is even

    BigInteger temp;
    BigInteger nMinusOne = n.subtract(BigInteger.ONE);

    loop: for(Integer base : a) {
      System.out.println("MR with base: " + base);
      temp = FME(BigInteger.valueOf(base), d, n);

      if( (temp.compareTo(BigInteger.ONE) == 0) || (temp.compareTo(nMinusOne) == 0) ) {
        continue;
        //return false; // probable prime
      }

      for(int r = 1; r < S; r++) {
        temp = FME(BigInteger.valueOf(base), d.multiply(BigInteger.valueOf(2).pow(r)), n);

        if(temp.compareTo(nMinusOne) == 0)
          continue loop;
          //return false; // probable prime
      }

      return true; // composite
    }

    return false; // prime, maybe
  }


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

  //EEA

  private void keyGen(int bitLength) {
    // generate 2 random big prime ints
    BigInteger p = genRandomBigPrime(bitLength);
    BigInteger q;
    do {
      q = genRandomBigPrime(bitLength);
    } while (p.compareTo(q) == 0);

    // generate e

    // calculate d

  }

  // ENC

  // DEC

  public static void main(String[] args) {
    // test
    MyRSA asd = new MyRSA();
    BigInteger a = BigInteger.valueOf(2).pow(35);
    BigInteger b = new BigInteger("561");

    //System.out.println(asd.genRandomBigInt(2));

    // generate key

    // user input
    BigInteger c = a;
    c = c.add(BigInteger.ONE);
    System.out.println("a + b = " + a.add(b).toString());
    System.out.println("a = " + a.toString());
    System.out.println("c = " + c.toString());
    clearConsole();


    System.out.println("c = " + c.toString(2));
    //String text=System.console().readLine();
    System.out.println( "mod: " + asd.FME( BigInteger.valueOf(7), BigInteger.valueOf(256), BigInteger.valueOf(13) ) );

    ArrayList<Integer> al = new ArrayList<Integer>();
    al.add(2);
    al.add(3);
    al.add(4);
    //asd.setBasesForMR(al);
    System.out.println(asd.MRTest(new BigInteger("7")));

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
