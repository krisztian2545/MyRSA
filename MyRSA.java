// run and tested on linux

import java.util.Scanner;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import java.io.*; // remove this

public class MyRSA {

  private final int MIN_NUM_OF_BASES = 3;
  private final int BITLENGTH_MULTIPLIER = 1.5f;

  private BigInteger[] PK = new BigInteger[2];
  private BigInteger SK;
  private ArrayList<BigInteger> bases = new ArrayList<BigInteger>();

  private void setBasesForMR(ArrayList<BigInteger> a) {
    bases = a;
  }

  private void clearBases() {
    bases.clear();
  }

  private int calcKeyBitLength(String m) {
    return (new BigInteger(m.getBytes(StandardCharsets.UTF_8))).bitLength();
  }

  private void printKeys() {
    System.out.println("PK = (" + PK[0] + ", " + PK[1] + ")");
    System.out.println("SK = (" + SK.toString() + ")");
  }

  private int randomIntLessThan(BigInteger limit, boolean makeOdd) {
    int x;
    SecureRandom sr = new SecureRandom();
    System.out.println("limit = " + limit.intValue() + "\n limit: " + limit.toString());
    do {
      x = sr.nextInt(limit.intValue());
      if(makeOdd && (x % 2 == 0))
        x++;
      System.out.println("? ... " + x);
    } while( (BigInteger.valueOf(x).compareTo(limit) == 1) || (x < 2) ); // 1 < x < limit

    System.out.println("x = " + x);
    System.out.println("limit = " + limit.intValue());

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

  private BigInteger genRandomBigIntLessThan(BigInteger limit, boolean makeOdd) {
    BigInteger x;
    SecureRandom sr = new SecureRandom();
    do{
      x =  new BigInteger(limit.bitLength(), sr);
      if((x.remainder(new BigInteger("2")).intValue() == 0) && makeOdd)
        x = x.add(BigInteger.ONE);

    } while((x.compareTo(limit) == 1) || (x.compareTo(BigInteger.valueOf(2)) == -1)); // 1 < x < limit

    return x;
  }

  private BigInteger genRandomBigInt(int bitLength) {
    BigInteger x;
    SecureRandom sr = new SecureRandom();
    do{
      x =  new BigInteger(bitLength, sr);
    } while(x.compareTo(BigInteger.ONE) < 1); // 1 < x < limit

    return x;
  }

  private BigInteger genRandomBigPrime(int bitLength) {
    BigInteger x;
    SecureRandom random = new SecureRandom();

    do {
      //x = new BigInteger(bitLength, random);
      x = genRandomBigInt(bitLength);

      //for performance
      if(x.remainder(new BigInteger("2")).intValue() == 0)
        x = x.add(BigInteger.ONE);

      System.out.println("test rpime: " + x.toString());
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
      BigInteger x;
      do {
        x = genRandomBigIntLessThan(n);
      } while(bases.contains(x));
      bases.add(x);
    }

    return MRWithBases(n, bases);
  }

  private boolean MRWithBases(BigInteger n, ArrayList<BigInteger> a) {
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
      System.out.println("MR with base: " + base.toString());
      temp = FME(base, d, n);

      if( (temp.compareTo(BigInteger.ONE) == 0) || (temp.compareTo(nMinusOne) == 0) ) {
        continue;
        //return false; // probable prime
      }

      for(int r = 1; r < S; r++) {
        temp = FME(base, d.multiply(BigInteger.valueOf(2).pow(r)), n);

        if(temp.compareTo(nMinusOne) == 0)
          continue loop;
          //return false; // probable prime
      }
      System.out.println("comp");
      return true; // composite
    }
    System.out.println("prime?");
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
  private HashMap<String, BigInteger> EEA(BigInteger a, BigInteger b) {
    HashMap<String, BigInteger> out = new HashMap<String, BigInteger>();
    int k = 0;
    BigInteger qk;
    BigInteger xk0 = BigInteger.ONE;
    BigInteger xk1 = BigInteger.ZERO;

    do {
      BigInteger[] temp = a.divideAndRemainder(b);
      qk = temp[0];
      a = b;
      b = temp[1];

      BigInteger xTemp = xk1.multiply(qk).add(xk0);
      xk0 = xk1;
      xk1 = xTemp;

      k++;
    } while(b.compareTo(BigInteger.ZERO) != 0);

    out.put("lnko", a);
    out.put("x", xk0.multiply(BigInteger.valueOf(-1).pow(k)));

    return out;
  }

  private void keyGen(int bitLength) {
    // generate 2 random big prime ints
    BigInteger p = genRandomBigPrime(bitLength);
    BigInteger q;
    do {
      q = genRandomBigPrime(bitLength);
    } while (p.compareTo(q) == 0);

    System.out.println("p = " + p.toString());
    System.out.println("q = " + q.toString());

    BigInteger n = p.multiply(q);
    BigInteger fiN = p.subtract(BigInteger.ONE).multiply( q.subtract(BigInteger.ONE) );
    System.out.println("fiN = " + fiN.toString());

    // generate e
    HashMap<String, BigInteger> hm;
    BigInteger e;
    do {
      //e = BigInteger.valueOf( randomIntLessThan(fiN, true) );
      e = genRandomBigIntLessThan(fiN, true);
      System.out.println("e = " + e.toString());
      hm = EEA(e, fiN);
    } while(hm.get("lnko").compareTo(BigInteger.ONE) != 0);

    // calculate d
    BigInteger d;
    if(hm.get("x").compareTo(BigInteger.ZERO) == -1)
      d = hm.get("x").add(fiN);
    else
      d = hm.get("x");

    System.out.println("d = " + d.toString());

    PK[0] = n;
    PK[1] = e;
    SK = d;

  }

  // ENC
  private BigInteger encrypt(String m) {
    BigInteger message = new BigInteger(m.getBytes(StandardCharsets.UTF_8));
    if(message.compareTo(PK[0]) > -1) {
      System.out.println("The key must be greater than the message!!!");
      return BigInteger.valueOf(-1);
    }

    return FME(message, PK[1], PK[0]);
    //return FME(m, PK[1], PK[0]);
  }

  private BigInteger forceEncrypt(String m) {
    BigInteger message = new BigInteger(m.getBytes(StandardCharsets.UTF_8));
    BigInteger out;

    while(message.compareTo(PK[0]) > -1) {
      keyGen(calcKeyBitLength(m));
    }

    System.out.println("Encrypted with keys:");
    printKeys();

    return FME(message, PK[1], PK[0]);
  }

  // DEC
  private String decrypt(BigInteger c) {
    return new String( FME(c, SK, PK[0]).toByteArray(), StandardCharsets.UTF_8 );
    //return FME(c, SK, PK[0]);
  }

  public static void main(String[] args) {
    // test
    MyRSA asd = new MyRSA();
    asd.keyGen(80);
    BigInteger a = BigInteger.valueOf(2).pow(35);
    BigInteger b = new BigInteger("561");

    //System.out.println(asd.genRandomBigInt(2));

    // generate key

    // user input
    /*
    BigInteger c = a;
    c = c.add(BigInteger.ONE);
    System.out.println("a + b = " + a.add(b).toString());
    System.out.println("a = " + a.toString());
    System.out.println("c = " + c.toString());


    System.out.println("c = " + c.toString(2));
    //String text=System.console().readLine();
    System.out.println( "mod: " + asd.FME( BigInteger.valueOf(7), BigInteger.valueOf(256), BigInteger.valueOf(13) ) );

    ArrayList<Integer> al = new ArrayList<Integer>();
    al.add(2);
    //al.add(2);
    //al.add(2);
    asd.setBasesForMR(al);
    System.out.println(asd.MRTest(new BigInteger("5")));
    asd.clearBases();
    System.out.println("keygen----------------");
    asd.keyGen();
    System.out.println("keygen----------------");
    asd.printKeys();

    HashMap<String, BigInteger> hm = asd.EEA(BigInteger.valueOf(920), BigInteger.valueOf(1240));
    System.out.println("lnko = " + hm.get("lnko"));
    System.out.println("x = " + hm.get("x"));

    String mess = "fuck you da vinci";
    BigInteger qwe = new BigInteger(mess.getBytes());
    System.out.println( qwe.toString() );
    System.out.println( new String(qwe.toByteArray()) ); */

    System.out.println("-------------------------------------------------------------------");
    Scanner sc = new Scanner(System.in);
    String message = sc.nextLine();
    asd.keyGen( (new BigInteger(message.getBytes(StandardCharsets.UTF_8))).multiply(BigInteger.valueOf(10)).bitLength() );
    asd.printKeys();
    BigInteger cc = asd.encrypt(message);
    System.out.println( (new BigInteger(message.getBytes(StandardCharsets.UTF_8))).toString() );
    System.out.println(cc.toString());
    System.out.println( asd.decrypt(cc) );

    // encryption / decryption

  }
/*
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
*/
}
