
package tmp.uqam.stage.MinSpanningTree;


public class BaseCoupling implements Coupling{

    protected String firstFileName;

    protected String secondFileName;

    protected double score;

    public BaseCoupling(String firstFileName, String secondFileName, double score){
        this.firstFileName = firstFileName;
        this.secondFileName = secondFileName;
        this.score = score;
    }

    public String getFirstFileName(){
        return this.firstFileName;
    }

    public String getSecondFileName(){
        return this.secondFileName;
    }

    public double getScore(){
        return this.score;
    }

    public void setFirstFileName(String fileName){
        this.firstFileName = fileName;
    }

    public void setSecondFileName(String secondName){
        this.secondFileName = secondName;
    }

    public void setScore(double score){
        this.score = score;
    }

    @Override
    public String toString() {
        return "BaseCoupling{" +
                "firstFileName='" + firstFileName + '\'' +
                ", secondFileName='" + secondFileName + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseCoupling)) return false;

        BaseCoupling coupling = (BaseCoupling) o;

        if (Double.compare(coupling.score, score) != 0) return false;
        if (!firstFileName.equals(coupling.firstFileName)) return false;
        return secondFileName.equals(coupling.secondFileName);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = firstFileName.hashCode();
        result = 31 * result + secondFileName.hashCode();
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
