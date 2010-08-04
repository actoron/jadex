package deco4mas.examples.agentNegotiation.common.statistics;

public interface IStatistic
{
	 /**
     * Add a values
     * @param d  the new value.
     */
    public void addValue(double d);

    /**
     * Add a array of values
     *
     * @param values  array holding the new values to add
     */
    public void addValues(double[] values);

    /**
     * Returns the number of values
     * @return the number of values.
     */
    public Double getN();

    /**
     * Returns a string, witch represent the state of this object
     * @return String for serialization
     */
    public String getSerializationString();
    
    /**
     * Clears the Statistic
     */
    public void clear();
}
