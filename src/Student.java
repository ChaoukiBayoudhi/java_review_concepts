import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

class Main {
    public static void main(String[] args) {
        Group g1=new Group();
        for (int i = 0; i < 3; i++) {
            Student s1=new Student();
            s1.getStudent();
            g1.addStudent(s1);
        }
        System.out.println("List of students :");
        g1.list();
        Scanner scanner=new Scanner(System.in);
        System.out.print("id of the student to found = ");
        int id=scanner.nextInt();
        Optional<Student> result=g1.getStudent(id);
        if (result.isPresent()) {
            System.out.println("The student information is " + result.get());
        }
        else
            System.out.println("The student was not found.");


        for(int i=0; i<2;i++){
            Student s1=new MasterStudent();
            s1.getStudent();
            g1.addStudent(s1);
        }
        g1.list();
        System.out.println("Sorted by Marks:");
        g1.sortStudentsByMarksAvg();
        g1.list();
    }//end main
}
enum StudentLevel {
    PRIMARY_CYCLE,
    SECONDARY_CYCLE,
    THIRD_CYCLE,
    DOCTORAL_CYCLE
}
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"marks","photo"})
@EqualsAndHashCode(of="id")
@SuperBuilder
public class Student implements Comparable<Student>{
    private int id;
    private String name;
    private List<Double> marks=new ArrayList<>();
    private  StudentLevel level;
    private byte[] photo;
    protected double guard;

    public void getStudent(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("id = ");
        id= scanner.nextInt();
        System.out.print("name = ");
        name= scanner.nextLine();
        name= scanner.nextLine();
        System.out.print("level = (PRIMARY_CYCLE,\n" +
                "    SECONDARY_CYCLE,\n" +
                "    THIRD_CYCLE,\n" +
                "    DOCTORAL_CYCLE)");
        String stdLevel= scanner.next().toUpperCase();
        level=StudentLevel.valueOf(stdLevel);
    }
    public  void evalGuard(){
        double s=0;
        for (double x:marks){
            s+=x;
        }
        guard=s/marks.size();
    }

    @Override
    public int compareTo(Student st) {
        //return (int)(guard - st.guard); //because if the abs(difference) is less than 1 we will return get zero
        if (guard==st.guard)
            return 0;
        if (guard<st.guard)
                return -1;
        return 1;
    }
}
@Getter
@Setter
class Group{
    private String name;
    private List<Student> students=new ArrayList<>();
    public void list(){
        System.out.println(students);
    }
    public void addStudent(Student student){
        try {
            if (students.contains(student))
                throw new ExistStudentException("Student with id =" + student.getId() + " already exists");

            students.add(student);
        }catch (ExistStudentException e) {
            System.out.println(e.getMessage());
            //or
            //e.printStackTrace();
        }
    }
    public Optional<Student> getStudent(int id) {
        Student st1=new Student();
        st1.setId(id);
        int index=students.indexOf(st1);
        Optional<Student> st=Optional.empty();
        if(index!=-1)
            st=Optional.of(students.get(index));
        return st;
    }
    public double avgMarks(){
        double s=0;
        for (Student st:students) {
            s+=st.getGuard();
        }
        return s/students.size();
    }
    public void sortStudentsByMarksAvg(){
        Collections.sort(students);
    }
}
class ExistStudentException extends Exception{
    public ExistStudentException(String message){
        super(message);
    }
}
@Getter
@Setter
class Module{
    private String name;
    private float dueTime;
    private String description;
}
class HighSchool{
    private Set<Group> groups=new TreeSet<>();
    public Map<String,Double> getMarksAVGByGroup(){
        Map<String,Double> map=new TreeMap<>();
        for(Group group : groups)
            map.put(group.getName(),group.avgMarks());
        return map;
    }
    public void printMaksByGroup(){
        Map<String, Double> result=getMarksAVGByGroup();
        for(Map.Entry<String,Double> entry : result.entrySet()){
            System.out.println("group name : " + entry.getKey() + "maks average : " + entry.getValue());
        }
    }
}
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
class MasterStudent extends Student{
    private Map<Module, Double> modulesMarks=new HashMap<>();
    public void addPresentationMark(Module module,double mak){
        modulesMarks.put(module,mak); //add new couple (module, mark) or update the mark if the module already exists
    }
    //redefinition
    @Override
    public void evalGuard(){
        double s=0;
        for(double x :modulesMarks.values()){
            s+=x;
        }
        double avgPresentations=s/modulesMarks.size();
        super.evalGuard();
        guard=guard*0.7 + avgPresentations*0.3;
    }
}