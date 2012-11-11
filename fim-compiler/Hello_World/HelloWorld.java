import com.twitter.raptortech97.git.rand.fimcompiler.Princess_Celestia;
// AUTO-GENERATED CLASS
public class HelloWorld extends Princess_Celestia{
public static void main(String[] args){ important_lesson_about_friendship(); }
public static void important_lesson_about_friendship(){

double Spike=67.428;
System.out.println(Spike);

double Rarity=Spike;

Rarity /= 3;
System.out.println(Rarity);

Rarity += 2;
System.out.println(Rarity);

Rarity *= Spike;
System.out.println(Rarity);

double X;
X = sum_of_everything_from_one_to_n(3);
System.out.println(X);

// Did you know that _test_ was the argument true
// _test_ then became whether 4000 was greater than the result of the _sum of everything from one to n_ using 100
// I said _test_

double n=2;
double[] book = new double[10];
book[(int)0] = 6;
book[(int)1] = 10;
book[(int)n] = -8;
book[(int)3] = -2;
X = sum_a_set_of_numbers(book);
System.out.println(X);
System.out.println(" ");
find_all_three_sums(book,4);
System.out.println(" ");

perform_Applejacku0027s_Drinking_Song();

}


public static void find_all_three_sums(double[] X,double length){
double first_counter=0;
while(first_counter<length){
double second_counter=0;
while(second_counter<length){
double third_counter=0;
while(third_counter<length){

//I said _first counter_" "_second counter_" "_third counter_

double sum=0;
sum += X[(int)first_counter];
sum += X[(int)second_counter];
sum += X[(int)third_counter];

if(sum==0){
System.out.println(X[(int)first_counter]+","+X[(int)second_counter]+","+X[(int)third_counter]);
}

third_counter += 1;
}
second_counter += 1;
}
first_counter += 1;
}
}


public static double sum_of_everything_from_one_to_n(double n){
double sum=0;
double current_number=1;
boolean test=false;
test = current_number<=n;

while(test){
sum += current_number;
current_number += 1;
test = current_number<=100;
}

return sum;
}

// This is a comment

public static void perform_Applejacku0027s_Drinking_Song(){

double Applejack=3;
boolean test=true;

while(Applejack>1){
System.out.println(Applejack+" jugs of cider on the wall, "+Applejack+" jugs of cider,");
Applejack -= 1;

if(Applejack>1){
System.out.println("Take one down and pass it around, "+Applejack+" jugs of cider on the wall.");

} else if(Applejack==1){
System.out.println("Take one down and pass it around, 1 jug of cider on the wall.");
System.out.println("1 jug of cider on the wall, 1 jug of cider.");
System.out.println("Take one down and pass it around, no more jugs of cider on the wall.");

} else {
System.out.println("No more jugs of cider on the wall, no more jugs of cider. Go to the store and buy some more, 99 jugs of cider on the wall.");
}
}

// Twilight's drunken state truly frightened me, so I couldn't disregard her order to send you this letter. Who would
// 		have thought her first reaction to hard cider would be this... explosive? I need your advice, your help
// 		everything, on how to deal with her drunk... self. -Spike

}

public static double sum_a_set_of_numbers(double[] X){
double sum=0;
sum += X[(int)1];
sum += X[(int)2];
return sum;
}

} // Author: Twilight Sparkle
