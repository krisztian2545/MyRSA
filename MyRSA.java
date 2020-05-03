import java.util.Scanner;
import java.math.BigInteger;
import java.security.SecureRandom;

public class MyRSA {

  private BigInteger SK, PK;

  private boolean MRTest(BigInteger x){
    return false;
  }

  private BigInteger genRandomBigInt(int bitLength) {
    return new BigInteger(bitLength, new SecureRandom());
  }

  private BigInteger genRandomBigPrimeInt(int bitLength) {
    BigInteger temp = genRandomBigInt(bitLength);
    SecureRandom random = new SecureRandom();

    do {
      temp = new BigInteger(bitLength, random);
    } while( MRTest(temp) );

    return temp;
  }

  private void keyGen(int bitLength) {
    // generate 2 random big prime ints
    BigInteger p = genRandomBigInt(bitLength);
    BigInteger q = genRandomBigInt(bitLength);


  }

  public static void main(String[] args) {
    // test
    MyRSA asd = new MyRSA();
    System.out.println(asd.genRandomBigInt(3));

    // generate key

    // user input
    System.out.println("asd");

    // encryption / decryption

  }

}
