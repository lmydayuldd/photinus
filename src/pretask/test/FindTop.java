package pretask.test;

public class FindTop {

	private int[] numbers;

	public FindTop(int[] numbersArg){
		this.numbers = numbersArg;
	}

	public int findHighest(int lowIndex, int highIndex){
		int top = this.numbers[lowIndex];
		for(int i = lowIndex; i <= lowIndex; i++){
			if (top < this.numbers[i]) 
				top = this.numbers[i];
		}
		return top;
	}
	
	public static void main(String[] args) {
		int myNumbers[] = {10, 5, 2, 4, 8};
		FindTop numbers = new FindTop(myNumbers);
		System.out.println( numbers.findHighest(0, 5) );
	}
	
}

//FINDTOP.JAVA ANSWERS
//What is the output of the code above?
//-1, 0, 2, 4, 8
//A: 8

//What would have been the output if we had "numbers.findHighest(1,1)" at line 21?
//10, 5, 2, 4, 8
//A: 5

//What would have been the output if instead of "int i=lowIndex;" we had "int i=0;" at line 11?
//10, 5, 2, 4, 8
//A: 10

//What would have been the output if we had "return i;" at line 13? 
//10, 5, 2, 4, 8
//A: 4

//What line in the program would have caused an ArrayIndexOutOfBounds exception if we  had "numbers.findHighest(0,5)" at line 21?
//6, 10, 11, 12, 13
//A: 12	