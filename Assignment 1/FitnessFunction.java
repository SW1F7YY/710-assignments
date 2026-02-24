public class FitnessFunction {
    public double calculateFitness(Node rootNode){
        System.out.println("starting fitness calculation");
        if (rootNode.type.equals("terminal")) return Double.parseDouble(rootNode.value);

        System.out.println("Entering recursion");
        String calculation = calculateFitnessHelper(rootNode);
        System.out.println("Final String:" + calculation);
        return 0;
    };

    public String calculateFitnessHelper(Node currentNode) {

        if (currentNode.type.equals("terminal")) return currentNode.value;
        StringBuilder returnString = new StringBuilder();
        for ( int i = 0; i < currentNode.children.size(); i++){
            // check the arity and start building the correct string
            if (i == 1) returnString.append(currentNode.value);
            returnString.append("(");
            if (currentNode.children.get(i).type.equals("terminal")) returnString.append(currentNode.children.get(i).value);
            else {
                returnString.append(calculateFitnessHelper(currentNode.children.get(i)));
            }
        }
        returnString.append(")");
        return returnString;
    }
}
