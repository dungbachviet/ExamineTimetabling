/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import gui_timetabling.Line;
import gui_timetabling.MultipleLinesChart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.basic.FuncVarConst;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.element.Element;
import localsearch.functions.element.ElementTmp;
import localsearch.functions.max_min.Max;
import localsearch.functions.max_min.Min;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;
import util.DataIO;
import util.UtilManager;
import static util.UtilManager.randomDouble;

/**
 *
 * @author DungBachViet
 */
public class ExamineTimetablingProblem {

    //==============
    public static ArrayList<Double> bestFitness;
    public static ArrayList<Double> timeRun;
    public static ArrayList<ArrayList<Double>> violationList;
    public static ArrayList<ArrayList<Double>> fitnessList2;
    public static ArrayList<ArrayList<Double>> fitnessList3;

    public static int maxExamGap;
    public static int minDisproportion;
    public static int maxSuitableTimeSlot;
    public static int maxDistributeDifficulty;

    public static int tabulen = 20;
    public static int maxTime = 70;
    public static int maxIter = 200000;
    public static int maxStable = 200;
    public static double initialTemp = 5000;
    public static double endingTemp = 0.05;
    
    public static double desiredFitness = 0.5;

//    tabulen = 20;
//        maxTime = 10;
//        maxIter = 100000;
//        maxStable = 200;
    //==============
    ExamineTimetablingManager manager;

    public HashMap<String, Area> hmIDToArea;
    public HashMap<String, Course> hmIDToCourse;
    public HashMap<String, ExamClass> hmIDToExamClass;
    public HashMap<String, Room> hmIDToRoom;
    public HashMap<String, Teacher> hmIDToTeacher;

    public ArrayList<Integer> availableDayList; // ds các ngày có thể tổ chức thi
    public ArrayList<Integer> jamLevelList; // ds mức độ tắc nghẽn của các kíp thi

    public int[][] commonStudents;
    public int[] roomSlots;
    public int numExamClass;
    public int numTeacher;
    public int numCourse;
    public int numRoom;
    public int numArea;

    ArrayList<Area> areas;
    ArrayList<Course> courses;
    ArrayList<ExamClass> examClasses;
    ArrayList<Room> rooms;
    ArrayList<Teacher> teachers;
    Set<Integer> setAvailableDay;
    ArrayList<ArrayList<Integer>> sameCourseCodeClass;

    LocalSearchManager ls;
    ConstraintSystem S;

    VarIntLS[] examDays;
    VarIntLS[] examTimeSlots;
    VarIntLS[] examRooms;
    VarIntLS[][] examClassToTeacher;

    IFunction examGapObj; // Maximize the minimum of gap between 2 exam times
    IFunction consecutiveObj; // Maximize the consecutive time for teacher
    IFunction balanceAreaObj; // Balance rooms at each timeslot for exam areas
    IFunction utilizeRoomObj; // Utilize maximally the capacity of each room, avoid wasting
    IFunction distributeDifficultyObj; // Distribute difficult degree throughout exam process
    IFunction disproportionObj; // Avoid disproportinating between number of student and room's capacity
    IFunction suitableTimeSlotObj; // Avoid helding much exams on traffic jam time

    public int numCommonClass;

    public ExamineTimetablingProblem() {

//        manager = UtilManager.generateData(1);
        manager = DataIO.readObject("src/dataset_timetabling/test_data.txt");

        DataIO.writeObject("timetabling_data", manager);

        maxExamGap = manager.getGapThreshold();
        minDisproportion = manager.getDisproportinationThreshold();
        maxSuitableTimeSlot = manager.getTrafficJamThreshold();
        maxDistributeDifficulty = manager.getDifficultExamThreshold();

//        maxExamGap = 1;
//        minDisproportion = 1;
//        maxSuitableTimeSlot = 1;
//        maxDistributeDifficulty = 1;
        System.out.println("maxExamGap : " + maxExamGap);
        System.out.println("minDisproportion : " + minDisproportion);
        System.out.println("maxSuitableTimeSlot : " + maxSuitableTimeSlot);
        System.out.println("maxDistributeDifficulty : " + maxDistributeDifficulty);

//        manager = DataIO.readObject("timetabling_data");
        hmIDToArea = manager.getHmIDToArea();
        hmIDToCourse = manager.getHmIDToCourse();
        hmIDToExamClass = manager.getHmIDToExamClass();
        hmIDToRoom = manager.getHmIDToRoom();
        hmIDToTeacher = manager.getHmIDToTeacher();

        availableDayList = manager.getAvailableDayList();
        jamLevelList = manager.getJamLevelList();

        commonStudents = manager.calcNumberCommonStudentOfClasses();
        roomSlots = manager.getRoomSlots();
        numExamClass = manager.getNumExamClasses();
        numTeacher = manager.getNumTeachers();
        numCourse = manager.getNumCourses();
        numRoom = manager.getNumRooms();
        numArea = manager.getNumAreas();

        areas = manager.getAreaList();
        courses = manager.getCourseList();
        examClasses = manager.getExamClassList();
        rooms = manager.getRoomList();
        teachers = manager.getTeacherList();
        setAvailableDay = manager.getAvailableDaySet();
        sameCourseCodeClass = manager.getCommonExamClassCourseList();

        numCommonClass = 0;
        for (int classIndex1 = 0; classIndex1 < numExamClass - 1; classIndex1++) {
            for (int classIndex2 = classIndex1 + 1; classIndex2 < numExamClass; classIndex2++) {
//                if (commonStudents[classIndex1][classIndex2] > 0) {
//                    numCommonClass++;
//                }
                System.out.println(commonStudents[classIndex1][classIndex2]);

            }
        }

        System.out.println("********** " + numCommonClass);

    }

    public void stateModel() {

        ls = new LocalSearchManager();
        S = new ConstraintSystem(ls);

        // Set up the value domains for variables 
        examDays = new VarIntLS[numExamClass];
        examTimeSlots = new VarIntLS[numExamClass];
        examRooms = new VarIntLS[numExamClass];
        for (int index = 0; index < numExamClass; index++) {
            examDays[index] = new VarIntLS(ls, setAvailableDay);
            System.out.println("Domain : examDays : " + setAvailableDay);
            examTimeSlots[index] = new VarIntLS(ls, 0, 3);
            examRooms[index] = new VarIntLS(ls, 0, numRoom - 1);
        }

        examClassToTeacher = new VarIntLS[numExamClass][numTeacher];
        for (int examClassIndex = 0; examClassIndex < numExamClass; examClassIndex++) {
            for (int teacherIndex = 0; teacherIndex < 2; teacherIndex++) {
                examClassToTeacher[examClassIndex][teacherIndex] = new VarIntLS(ls, 0, numTeacher);
            }
        }

        // Room Capacity Constraint
        int numStudentPerClass;
        for (int examClassIndex = 0; examClassIndex < numExamClass; examClassIndex++) {
            numStudentPerClass = examClasses.get(examClassIndex).getEnrollmentList().size();
            S.post(new LessOrEqual(numStudentPerClass, new ElementTmp(roomSlots, examRooms[examClassIndex])));
        }

        // Constraint - same day and same time-slot if exam classes have the same course code
        int sameCourseCodeClasslength = sameCourseCodeClass.size();
        for (int courseIndex = 0; courseIndex < sameCourseCodeClasslength; courseIndex++) {
            ArrayList<Integer> listClasses = sameCourseCodeClass.get(courseIndex);

            // Get timeSlot and days of exam classes having common course code
            int length = listClasses.size();
            VarIntLS[] listTimeSlots = new VarIntLS[length];
            VarIntLS[] listDays = new VarIntLS[length];
            for (int index = 0; index < length; index++) {
                listTimeSlots[index] = examTimeSlots[listClasses.get(index)];
                listDays[index] = examDays[listClasses.get(index)];
            }

            S.post(new AND(
                    new IsEqual(new Max(listTimeSlots), new Min(listTimeSlots)),
                    new IsEqual(new Max(listDays), new Min(listDays))));
        }

        // Constraint : Don't allow to violate the busy list of rooms
        for (int roomIndex = 0; roomIndex < numRoom; roomIndex++) {
            ArrayList<TimeUnit> busyList = rooms.get(roomIndex).getBusyTimeList();
            for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
                for (int busyIndex = 0; busyIndex < busyList.size(); busyIndex++) {
                    // examRooms[classIndex]=roomIndex & examDays[classIndex] belongs to busy day ==> not same timeslot
                    S.post(new Implicate(
                            new AND(
                                    new IsEqual(examRooms[classIndex], roomIndex),
                                    new IsEqual(examDays[classIndex], busyList.get(busyIndex).getDay())
                            ),
                            new NotEqual(examTimeSlots[classIndex], busyList.get(busyIndex).getTimeSlot())
                    ));
                }
            }
        }

        // Constraint : Don't allow to violate the busy list of teachers
        for (int teacherIndex = 0; teacherIndex < numTeacher; teacherIndex++) {
            ArrayList<TimeUnit> busyList = teachers.get(teacherIndex).getBusyTimeList();
            for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
                for (int busyIndex = 0; busyIndex < busyList.size(); busyIndex++) {

                    S.post(new Implicate(
                            new AND(
                                    new OR(new IsEqual(examClassToTeacher[classIndex][0], teacherIndex),// is equal to teacher1
                                            new IsEqual(examClassToTeacher[classIndex][1], teacherIndex)),// is equal to teacher2
                                    new IsEqual(examDays[classIndex], busyList.get(busyIndex).getDay()) // same day
                            ),
                            new NotEqual(examTimeSlots[classIndex], busyList.get(busyIndex).getTimeSlot())// not same timeslot
                    ));
                }
            }
        }

        // Constraint : Don't allow any 2 exam classes to be placed at same room, same time
        // Constraint : Don't allow any 2 exam classes having any same teacher to be placed at same time
        // Constraint : Any 2 exam classes having any same student are at least 1 timeslot apart
        for (int classIndex1 = 0; classIndex1 < numExamClass - 1; classIndex1++) {
            for (int classIndex2 = classIndex1 + 1; classIndex2 < numExamClass; classIndex2++) {
                // Same room, same day ==> not same timeslot
                S.post(new Implicate(
                        new AND(
                                new IsEqual(examRooms[classIndex1], examRooms[classIndex2]), // same room
                                new IsEqual(examDays[classIndex1], examDays[classIndex2]) // saem day
                        ),
                        new NotEqual(examTimeSlots[classIndex1], examTimeSlots[classIndex2]) // not same timeslot
                ));

                // Same timeslot, same day ==> not any same teacher
                S.post(new Implicate(
                        new AND(
                                new IsEqual(examTimeSlots[classIndex1], examTimeSlots[classIndex2]), // same timeslot
                                new IsEqual(examDays[classIndex1], examDays[classIndex2]) // same day
                        ),
                        new AND( // 4 conditions : not any common teacher exists in 2 exam class at same time
                                new NotEqual(
                                        examClassToTeacher[classIndex1][1],
                                        examClassToTeacher[classIndex2][0]
                                ),
                                new AND(
                                        new NotEqual(
                                                examClassToTeacher[classIndex1][1],
                                                examClassToTeacher[classIndex2][1]
                                        ),
                                        new AND(
                                                new NotEqual(
                                                        examClassToTeacher[classIndex1][0],
                                                        examClassToTeacher[classIndex2][0]
                                                ),
                                                new NotEqual(
                                                        examClassToTeacher[classIndex1][0],
                                                        examClassToTeacher[classIndex2][1]
                                                ))
                                )
                        )
                ));

                // Same student, same day ==> |timeslot1 - timeslot2| >= 2
                VarIntLS[] convertIntToVarIntLS = new VarIntLS[2];
                convertIntToVarIntLS[0] = new VarIntLS(ls, 0, 0);
                convertIntToVarIntLS[1] = new VarIntLS(ls, commonStudents[classIndex1][classIndex2], commonStudents[classIndex1][classIndex2]);

                VarIntLS[] timeSlots = new VarIntLS[2];
                timeSlots[0] = examTimeSlots[classIndex1];
                timeSlots[1] = examTimeSlots[classIndex2];

                IFunction max = new Max(timeSlots);
                IFunction min = new Min(timeSlots);

                S.post(new Implicate(
                        new AND(
                                new LessThan(convertIntToVarIntLS[0], convertIntToVarIntLS[1]), // condition : exists common students
                                new IsEqual(examDays[classIndex1], examDays[classIndex2]) // same day
                        ),
                        new LessOrEqual(2, new FuncMinus(max, min)) // at least 1 timeslot apart
                ));

            }
        }

        // Constraint - Don't allow teacher to supervise any exam class having same courseID with her/him
        for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
            int courseID = examClasses.get(classIndex).getCourse().getCourseIDInt();
            VarIntLS convertIntToVarIntLS = new VarIntLS(ls, courseID, courseID);
            for (int teacherIndex = 0; teacherIndex < numTeacher; teacherIndex++) {
                ArrayList<Integer> courseList = teachers.get(teacherIndex).getTeachingCourseListIDInt();
                for (int courseIndex = 0; courseIndex < courseList.size(); courseIndex++) {

                    S.post(
                            new Implicate(
                                    new IsEqual(convertIntToVarIntLS, courseList.get(courseIndex)), // same courseID
                                    new AND(
                                            new NotEqual(examClassToTeacher[classIndex][0], teacherIndex), // the teacher can not be any one of 
                                            new NotEqual(examClassToTeacher[classIndex][1], teacherIndex) // two teachers assigned that exam class
                                    )
                            )
                    );

                }

            }
        }

        // Objective 1 : Maximize the minimum of gap between 2 exam times
        ArrayList<IFunction> commonExamGaps = new ArrayList<IFunction>();
        for (int classIndex1 = 0; classIndex1 < numExamClass - 1; classIndex1++) {
            for (int classIndex2 = classIndex1 + 1; classIndex2 < numExamClass; classIndex2++) {
                if (commonStudents[classIndex1][classIndex2] > 0) {
                    IFunction[] examTimes = new IFunction[2];

                    // Calculates Day[i]*4 + Timeslot[j]
                    examTimes[0] = new FuncPlus(new FuncMult(examDays[classIndex1], 4), examTimeSlots[classIndex1]);
                    examTimes[1] = new FuncPlus(new FuncMult(examDays[classIndex2], 4), examTimeSlots[classIndex2]);

                    commonExamGaps.add(new FuncMinus(new Max(examTimes), new Min(examTimes)));
                }
            }
        }

        IFunction[] commonExamGapsFunction = new IFunction[commonExamGaps.size()];
        for (int index = 0; index < commonExamGaps.size(); index++) {
            commonExamGapsFunction[index] = commonExamGaps.get(index);
        }

        examGapObj = new Min(commonExamGapsFunction); // maximize the minimum
//        examGapObj = new FuncVarConst(ls, 0);

        // Objective 2 : Avoid disproportination between exam class's number of student and room's capacity
        IFunction[] slotDisproportion = new IFunction[numExamClass];
        for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
            int classSlots = examClasses.get(classIndex).getEnrollmentList().size();
            slotDisproportion[classIndex] = new FuncMinus(new ElementTmp(roomSlots, examRooms[classIndex]), classSlots);
        }

        disproportionObj = new Max(slotDisproportion); // minimize the maximum

        // Objective 3 : Avoid helding much exams on traffic jam time
        IFunction[] timeSlotSuits = new IFunction[4];
        for (int timeSlotIndex = 0; timeSlotIndex < jamLevelList.size(); timeSlotIndex++) {
            // Number timeslot i * jamLevel[i]
            timeSlotSuits[timeSlotIndex] = new FuncMult(new ConditionalSum(examTimeSlots, timeSlotIndex), jamLevelList.get(timeSlotIndex));
        }
        suitableTimeSlotObj = new Sum(timeSlotSuits);

        // Objective 4 : Difficult courses should be examed nearly at last of exam process
        IFunction[] times = new IFunction[numExamClass];
        IFunction[] timeMultDifficult = new IFunction[numExamClass]; // time*difficultLevel
        for (int classIndex = 0; classIndex < numExamClass; classIndex++) {
            // Calculates time[i] = Day[i]*4 + timeSlot[i]
            times[classIndex] = new FuncPlus(new FuncMult(examDays[classIndex], 4), examTimeSlots[classIndex]);

            // Get difficult level of exam class
            int classDifficulty = examClasses.get(classIndex).getCourse().getDifficultLevel();
            timeMultDifficult[classIndex] = new FuncMult(times[classIndex], classDifficulty);
        }

        // Distribute difficult degree throughout exam process
        distributeDifficultyObj = new Sum(timeMultDifficult); // Maximize the sum

        ls.close();

    }

    public void solveTabu(int currTest) {

        MyLocalSearch ts = new MyLocalSearch();

        // Phase 1 : Find the feasible solution (constraint violation = 0)
        ts.TwoStagesGreedy(S, currTest);

        // Phase 2 : Improve the quality of solution (maintain the constraint violation to 0)
        IFunction[] obj = new IFunction[4];
        obj[0] = examGapObj;
        obj[1] = disproportionObj;
        obj[2] = suitableTimeSlotObj;
        obj[3] = distributeDifficultyObj;
        ts.myImprovingTabuSearchMaintainConstraints(obj, S, tabulen, maxTime, maxIter, maxStable, currTest);

        // Phase 3 : Continue improving the quality of solution
        ts.myHillClimbingSearchMaintainConstraints(obj, S, maxTime, maxIter, currTest);
    }

    public void solveAnnealing(int currTest) {

        MyLocalSearch ts = new MyLocalSearch();

        // Phase 1 : Find the feasible solution (constraint violation = 0)
        ts.TwoStagesGreedy(S, currTest);

        // Phase 2 : Improve the quality of solution (maintain the constraint violation to 0)
        IFunction[] obj = new IFunction[4];
        obj[0] = examGapObj;
        obj[1] = disproportionObj;
        obj[2] = suitableTimeSlotObj;
        obj[3] = distributeDifficultyObj;
        ts.myAnnealingSearchMaintainConstraints(obj, S, maxTime, maxIter, initialTemp, endingTemp, currTest);

        // Phase 3 : Continue improving the quality of solution
        ts.myHillClimbingSearchMaintainConstraints(obj, S, maxTime, maxIter, currTest);
    }

    public void solveDegratedCeiling(int currTest) {

        MyLocalSearch ts = new MyLocalSearch();

        // Phase 1 : Find the feasible solution (constraint violation = 0)
        ts.TwoStagesGreedy(S, currTest);

        // Phase 2 : Improve the quality of solution (maintain the constraint violation to 0)
        IFunction[] obj = new IFunction[4];
        obj[0] = examGapObj;
        obj[1] = disproportionObj;
        obj[2] = suitableTimeSlotObj;
        obj[3] = distributeDifficultyObj;
        ts.myDegratedCeilingSearchMaintainConstraints(obj, S, maxTime, maxIter, desiredFitness, currTest);

        // Phase 3 : Continue improving the quality of solution
        ts.myHillClimbingSearchMaintainConstraints(obj, S, maxTime, maxIter, currTest);
    }

    public static void main(String[] args) {
        ExamineTimetablingProblem Timetabling = new ExamineTimetablingProblem();
        System.out.println(Timetabling.manager.toString());

        // Set up problem's model
        Timetabling.stateModel();

        // set parameter
//        tabulen = 20;
//        maxTime = 10;
//        maxIter = 100000;
//        maxStable = 200;
        Timetabling.testBatchDegratedCeiling(1);
//
//        int[] tabuLenArr = new int[]{20, 40};
//        int[] maxTimeArr = new int[]{15, 20};
//        int[] maxIterArr = new int[]{30000, 50000};
//        int[] maxStableArr = new int[]{200, 300};
//        int numParameters = tabuLenArr.length;
//
//        // statistic
//        String dirSaveFile = "src/statistics/" + System.currentTimeMillis();
//        DataIO.makeDir(dirSaveFile);
//        int numTest = 2;
//
//        ArrayList<ArrayList<String>> outputList = new ArrayList<>();
//        ArrayList<String> colName = new ArrayList<>();
//        colName.add("ParameterID");
//        colName.add("TabuLen");
//        colName.add("maxTime");
//        colName.add("maxIter");
//        colName.add("maxStable");
//        colName.add("MinFitness");
//        colName.add("AverageFitness");
//        colName.add("MaxFitness");
//        colName.add("VarianceFitness");
//        colName.add("MinTime");
//        colName.add("AverageTime");
//        colName.add("MaxTime");
//        colName.add("VarianceTime");
//
//        outputList.add(colName);
//
//        for (int indexPara = 0; indexPara < numParameters; ++indexPara) {
//            String paraID = "Parameter" + (indexPara + 1);
//
//            // set parameter
//            tabulen = tabuLenArr[indexPara];
//            maxTime = maxTimeArr[indexPara];
//            maxIter = maxIterArr[indexPara];
//            maxStable = maxStableArr[indexPara];
//
//            Timetabling.testBatchTabu(numTest);
//
//
//            ArrayList<String> row1 = new ArrayList<>();
//
////                row1.add("Parameter1");
//            row1.add(paraID);
//            row1.add(String.valueOf(tabulen));
//            row1.add(String.valueOf(maxTime));
//            row1.add(String.valueOf(maxIter));
//            row1.add(String.valueOf(maxStable));
//
//            double minFitness = UtilManager.getMin(bestFitness);
//            double avgFitness = UtilManager.getAverage(bestFitness);
//            double maxFitness = UtilManager.getMax(bestFitness);
//            double varFitness = UtilManager.getVariance(bestFitness);
//
//            double minTime = UtilManager.getMin(timeRun);
//            double avgTime = UtilManager.getAverage(timeRun);
//            double maxTime = UtilManager.getMax(timeRun);
//            double varTime = UtilManager.getVariance(timeRun);
//
//            row1.add(String.valueOf(minFitness));
//            row1.add(String.valueOf(avgFitness));
//            row1.add(String.valueOf(maxFitness));
//            row1.add(String.valueOf(varFitness));
//
//            System.out.println("\n==>" + timeRun);
//            row1.add(String.valueOf(minTime));
//            row1.add(String.valueOf(avgTime));
//            row1.add(String.valueOf(maxTime));
//            row1.add(String.valueOf(varTime));
//            System.out.println("\nRow1 = " + row1);
//
//            outputList.add(row1);
//
//            boolean isShowGui = true;
//            if (isShowGui) {
//                // show violation_loop
//                MultipleLinesChart mlc = new MultipleLinesChart("Demo");
//                mlc.setxAxisLabel("Loop");
//                mlc.setyAxisLabel("Violation");
//                mlc.setChartTitle("Tabu algorithm");
//
//                ArrayList<Line> lineList = new ArrayList<>();
//                ArrayList<Double> xList = new ArrayList<>();
//                ArrayList<Double> yList = new ArrayList<>();
//
//                int index = 0;
//                yList = violationList.get(index);
//
//                for (int k = 0; k < yList.size(); ++k) {
//                    xList.add(1.0 * (k + 1));
//                }
//
//                Line lineViolation = new Line("Violation", xList, yList);
//                lineList.add(lineViolation);
//
//                mlc.setLineList(lineList);
//
//                String pathSaveImage = dirSaveFile + "/violation-loop_" + paraID + ".png";
//                mlc.renderGraph(false, pathSaveImage);
//
//                // show violation_loop
//                MultipleLinesChart mlc2 = new MultipleLinesChart("Demo");
//                mlc2.setxAxisLabel("Loop");
//                mlc2.setyAxisLabel("Violation");
//                mlc2.setChartTitle("Tabu algorithm");
//
//                ArrayList<Line> lineListFitness = new ArrayList<>();
//                ArrayList<Double> xList2 = new ArrayList<>();
//                ArrayList<Double> yList2 = new ArrayList<>();
//
//                int index2 = 0;
//                yList2 = fitnessList.get(index2);
//
//                for (int k = 0; k < yList2.size(); ++k) {
//                    xList2.add(1.0 * (k + 1));
//                }
//
//                Line lineFitness = new Line("Fitness", xList2, yList2);
//                lineListFitness.add(lineFitness);
//                mlc2.setLineList(lineListFitness);
//
//                String pathSaveImage2 = dirSaveFile + "/fitness-loop_" + paraID + ".png";
//                mlc2.renderGraph(false, pathSaveImage2);
//            }
//
//        }
//        // write excel file
//        String csvPath = dirSaveFile + "/" + Timetabling.manager.getDatasetName() + "_NumTest-" + numTest + ".csv";
//        DataIO.writeFileExcel(csvPath, outputList);
//        
//        // write infomation data file
//        String infoDataPath = dirSaveFile + "/info_data.txt";
//        DataIO.writeStringToFile(infoDataPath, Timetabling.manager.toString());

    }

    public static void testBatchTabu(int nbTrials, ExamineTimetablingProblem Timetabling) {
        if (Timetabling == null) {
            Timetabling = new ExamineTimetablingProblem();
        }

        bestFitness = new ArrayList<>(nbTrials);
        timeRun = new ArrayList<>(nbTrials);
        fitnessList2 = new ArrayList<>(nbTrials);
        fitnessList3 = new ArrayList<>(nbTrials);
        violationList = new ArrayList<>(nbTrials);
//        
        // nbTrials : number of trials (số lần chạy thử)
        // Mảng này lưu thời gian cho mỗi lần chạy thử
        double[] t = new double[nbTrials];
        double avg_t = 0;
        for (int currTest = 0; currTest < nbTrials; currTest++) {
            double t0 = System.currentTimeMillis();
            Timetabling.stateModel();
            Timetabling.solveTabu(currTest);
            t[currTest] = (System.currentTimeMillis() - t0) * 0.001;
            timeRun.add(currTest, t[currTest]);
            // Lưu tổng thời gian cho tất cả các lần chạy thử
            avg_t += t[currTest];
        }

        // Trung bình thời gian cho từng lần chạy thử
        avg_t = avg_t * 1.0 / nbTrials;
        System.out.println("Time = " + avg_t);
    }

    public static void testBatchAnnealing(int nbTrials, ExamineTimetablingProblem Timetabling) {
        if (Timetabling == null) {
            Timetabling = new ExamineTimetablingProblem();
        }

        bestFitness = new ArrayList<>(nbTrials);
        timeRun = new ArrayList<>(nbTrials);
        fitnessList2 = new ArrayList<>(nbTrials);
        fitnessList3 = new ArrayList<>(nbTrials);
        violationList = new ArrayList<>(nbTrials);
//        
        // nbTrials : number of trials (số lần chạy thử)
        // Mảng này lưu thời gian cho mỗi lần chạy thử
        double[] t = new double[nbTrials];
        double avg_t = 0;
        for (int currTest = 0; currTest < nbTrials; currTest++) {
            double t0 = System.currentTimeMillis();
            Timetabling.stateModel();
            Timetabling.solveAnnealing(currTest);
            t[currTest] = (System.currentTimeMillis() - t0) * 0.001;
            timeRun.add(currTest, t[currTest]);
            // Lưu tổng thời gian cho tất cả các lần chạy thử
            avg_t += t[currTest];
        }

        // Trung bình thời gian cho từng lần chạy thử
        avg_t = avg_t * 1.0 / nbTrials;
        System.out.println("Time = " + avg_t);
    }

    public static void testBatchDegratedCeiling(int nbTrials) {
        ExamineTimetablingProblem Timetabling = new ExamineTimetablingProblem();

        bestFitness = new ArrayList<>(nbTrials);
        timeRun = new ArrayList<>(nbTrials);
        fitnessList2 = new ArrayList<>(nbTrials);
        fitnessList3 = new ArrayList<>(nbTrials);
        violationList = new ArrayList<>(nbTrials);
//        
        // nbTrials : number of trials (số lần chạy thử)
        // Mảng này lưu thời gian cho mỗi lần chạy thử
        double[] t = new double[nbTrials];
        double avg_t = 0;
        for (int currTest = 0; currTest < nbTrials; currTest++) {
            double t0 = System.currentTimeMillis();
            Timetabling.stateModel();
            Timetabling.solveDegratedCeiling(currTest);
            t[currTest] = (System.currentTimeMillis() - t0) * 0.001;
            timeRun.add(currTest, t[currTest]);
            // Lưu tổng thời gian cho tất cả các lần chạy thử
            avg_t += t[currTest];
        }

        // Trung bình thời gian cho từng lần chạy thử
        avg_t = avg_t * 1.0 / nbTrials;
        System.out.println("Time = " + avg_t);
    }

}
