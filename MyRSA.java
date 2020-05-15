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
  private final int BITLENGTH_MULTIPLIER = 5;
  private final int BITLENGTH = 100;

  private BigInteger[] PK = new BigInteger[2];
  private BigInteger SK;
  private ArrayList<BigInteger> bases = new ArrayList<BigInteger>();

  private void setBasesForMR(ArrayList<BigInteger> a) {
    clearBases();
    bases = a;
  }

  private void clearBases() {
    bases.clear();
  }

  private int calcKeyBitLength(String m) {
    return (new BigInteger(m.getBytes(StandardCharsets.UTF_8))).bitLength() + BITLENGTH_MULTIPLIER;
  }

  private void printKeys() {
    System.out.println("PK = (" + PK[0] + ", " + PK[1] + ")");
    System.out.println("SK = (" + SK + ")");
  }

  private void setPK(String n, String e) {
    PK[0] = new BigInteger(n);
    PK[1] = new BigInteger(e);
  }

  private void setSK(String key) {
    SK = new BigInteger(key);
  }

  /*private int randomIntLessThan(BigInteger limit, boolean makeOdd) {
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
  }*/

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

  private void keyGen() {
    keyGen(BITLENGTH);
  }

  // ENC
  private String encrypt(String m) {
    BigInteger message = new BigInteger(m.getBytes(StandardCharsets.UTF_8));
    if(message.compareTo(PK[0]) > -1) {
      System.out.println("The key must be greater than the message!!!");
      return "-1";
    }

    return FME(message, PK[1], PK[0]).toString();
    //return FME(m, PK[1], PK[0]);
  }

  private String forceEncrypt(String m) {
    /*BigInteger message = new BigInteger(m.getBytes(StandardCharsets.UTF_8));
    //BigInteger out;

    while(message.compareTo(PK[0]) > -1) {
      keyGen(calcKeyBitLength(m));
    }

    System.out.println("Encrypted with keys:");
    printKeys();

    return FME(message, PK[1], PK[0]).toString();*/
    if(SK == null)
      keyGen(calcKeyBitLength(m));

    String out = encrypt(m);
    while(out == "-1") {
      keyGen(calcKeyBitLength(m));
    }
    System.out.println("Encrypted with keys:");
    printKeys();

    return out;
  }

  // DEC
  private String decrypt(String c) {
    if(c == "-1"){
      System.out.println("Can't decrypt the message, because there was a problem with the encryption!");
      return "Can't decrypt the message, because there was a problem with the encryption!";
    }
    return new String( FME(new BigInteger(c), SK, PK[0]).toByteArray(), StandardCharsets.UTF_8 );
    //return FME(c, SK, PK[0]);
  }

  // ----------------------------------------------------------------------------------------
  // -- MAIN --  -- MAIN -- -- MAIN -- -- MAIN -- -- MAIN -- -- MAIN -- -- MAIN -- -- MAIN --
  // ----------------------------------------------------------------------------------------
  public static void main(String[] args) {
    //test();

    MyRSA temp = new MyRSA();
    MyRSA rsa = new MyRSA();
    rsa.keyGen();

    String lastEncrypted = "";
    String lastDecrypted = "";

    // welcome
    System.out.println("Welcome to my RSA encryptor / decryptor.\nType \'list\' to see the commands.\n");

    boolean loop = true;
    do {

      System.out.println();
      String[] command = scanner.nextLine().trim().split(" ");

      switch(command[0]) {
        case "enc":
        case "encrypt":
          if(command.length == 1) {
            printGuide();
            break;
          }
          lastEncrypted = rsa.encrypt( getRange(command, 1, command.length-1) );
          System.out.println( lastEncrypted );
          break;

        case "enc2":
        case "encrypt2":
          if(command.length < 4) {
            printGuide();
            break;
          }
          temp.setPK(command[1], command[2]);
          System.out.println( temp.encrypt( getRange(command, 3, command.length-3) ) );
          break;

        case "fenc":
        case "forceEncrypt":
          lastEncrypted = rsa.forceEncrypt(getRange(command, 1, command.length-1));
          System.out.println( lastEncrypted );
          break;

        case "encl":
        case "encryptLast":
          if(command.length != 1) {
            printGuide();
            break;
          }
          if(lastDecrypted == "") {
            System.out.println("There is no stored decrypted message.");
            break;
          }
          lastEncrypted = rsa.encrypt(lastDecrypted);
          System.out.println( lastEncrypted );
          break;

        case "dec":
        case "decrypt":
          if(command.length != 2) {
            printGuide();
            break;
          }
          lastDecrypted = rsa.decrypt(command[1]);
          System.out.println( lastDecrypted );
          break;

        case "dec2":
        case "decrypt2":
          if(command.length != 4) {
            printGuide();
            break;
          }
          temp.setPK(command[2], "3");
          temp.setSK(command[1]);
          System.out.println(temp.decrypt(command[3]));
          break;

        case "decl":
        case "decryptLast":
          if(command.length != 1) {
            printGuide();
            break;
          }
          if(lastEncrypted == "") {
            System.out.println("There is no stored encrypted message.");
            break;
          }
          lastDecrypted = rsa.decrypt(lastEncrypted);
          System.out.println(lastDecrypted);
          break;

        case "gen":
        case "generate":
          if(command.length == 2) {
            rsa.keyGen();
            rsa.printKeys();
          } else if(command.length == 3){
            rsa.keyGen(Integer.parseInt(command[2]));
            rsa.printKeys();
          } else {
            printGuide();
          }
          break;

        case "pk":
        case "PK":
          if(command.length != 3) {
            printGuide();
            break;
          }
          rsa.setPK(command[1], command[2]);
          break;

        case "sk":
        case "SK":
          if(command.length != 2) {
            printGuide();
            break;
          }
          rsa.setSK(command[1]);
          break;

        case "mr":
        case "MR":
          if(command.length < 2) {
            printGuide();
            break;
          }
          if(command.length > 2) {
            ArrayList<BigInteger> ar = new ArrayList<BigInteger>();
            for(int i = 2; i < command.length; i++)
              ar.add( new BigInteger(command[i]) );
            temp.setBasesForMR(ar);
          }
          System.out.println( temp.MRTest(new BigInteger(command[1])) ? "The given number is composite." : "The given number is probably a prime." );
          break;

        case "fme":
        case "FME":
          if(command.length < 4) {
            printGuide();
            break;
          }
          System.out.println(temp.FME( new BigInteger(command[1]), new BigInteger(command[2]), new BigInteger(command[3]) ).toString() );
          break;

        case "eea":
        case "EEA":
          if(command.length < 3) {
            printGuide();
            break;
          }
          HashMap<String, BigInteger> hs = temp.EEA(new BigInteger(command[1]), new BigInteger(command[2]));
          System.out.println("Result: " + hs.get("lnko").toString());
          System.out.println("X: " + hs.get("x").toString());
          break;

        case "l":
        case "list":
          printCommands();
          break;

        case "exit":
          loop = false;
          break;

        default:
          printGuide();
      }

    } while(loop);
  }

  // -----------------------------------------------------------------------------------------
  // -- END -- -- END -- -- END -- -- END -- -- END -- -- END -- -- END -- -- END -- -- END --
  // -----------------------------------------------------------------------------------------

  static void test() {
    // test
    MyRSA asd = new MyRSA();
    //asd.keyGen(80);
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
    //asd.keyGen( asd.calcKeyBitLength(message) );
    asd.printKeys();
    String cc = asd.forceEncrypt(message);
    System.out.println("message as BigInteger: " + (new BigInteger(message.getBytes(StandardCharsets.UTF_8))).toString() );
    System.out.println("cc: " + cc);
    System.out.println( asd.decrypt(cc) );

    // encryption / decryption
  }

  //---------------------------------------------- User Interface -------------------------------
  private static Scanner scanner = new Scanner(System.in);

  private static void printCommands() {
    String s = " ----------------------------------------\n";
    s += "encrypt <message> - encrypts the given message\n";
    s += "encrypt2 <modulus> <exponent> <message> - encrypts the given message with the given key (the key will be forgotten)\n";
    s += "forceEncrypt - if the message is bigger than the key, than itt will generate new keys and use them to encrypt the message\n";
    s += "encryptLast - encrypts the last decrypted message\n";
    s += "decrypt <encrypted message> - decrypts the message\n";
    s += "decrypt2 <private key> <modulus> <encrypted message> - decrypts the message with the given key (the key will be forgotten)\n";
    s += "decryptLast - decrypts the last encrypted message\n";
    s += "generate keys <bitlength> - generate new keys, the bitlength is optional to set an upper limit when generating primes\n";
    s += "PK <modulus> <exponent> - (for advanced users) set public key, should be followed by setting the private key\n";
    s += "SK <private key> - (for advanced users) set private key, first you must to set the public key!!!\n";
    s += "MR <number> <base> <base> .. - tests the given number with the Miller Rabin test, optionally you can give the bases to work with\n";
    s += "FME <base> <exponent> <modulus> - Fast Modular Exponentiation\n";
    s += "EEA <a> <b> - Extended Euclidean Algorithm\n";
    s += "exit - exit the program\n";
    s += " -----------------------------------------\n";
    System.out.println(s);
  }

  private static void printGuide() {
    System.out.println("You misstyped something. \nType \'list\' to see the commands.\n");
  }

  private static String getRange(String[] arr, int start, int length) {
    String out = arr[start];
    for(int i = 1; i < length; i++)
      out += " " + arr[start + i];

    return out;
  }

  // -------------------------------------- just logger class


}

class Logger {
  private static boolean enabled = true;

  public static void enable() {
    enabled = true;
  }

  public static void disable() {
    enabled = false;
  }

  public static void log(String message) {
    if(enabled)
      System.out.println(message);
  }
}
