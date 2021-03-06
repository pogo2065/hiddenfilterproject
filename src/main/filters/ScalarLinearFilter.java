package main.filters;

import main.filters.interfaces.ResetableFilter;
import main.filters.interfaces.ScalarFilter;
import main.utils.Buffer;

/**
 * A Scalar Linear Filter is defined by two arrays, A and B, which contain
 * constants that are used to compute the new output. In order to compute the
 * outputs, the last |A|-1 inputs and |B| outputs are multiplies by their
 * relevant constants and then the difference becomes the next output.
 * 
 * @author james
 *
 */
public class ScalarLinearFilter implements ScalarFilter,
		ResetableFilter<Double> {

	private final Buffer<Double> inputBuffer;
	private final Buffer<Double> outputBuffer;
	private final double[] a;
	private final double[] b;

	protected Double result = null;

	private final Double resetMultiplier;

	/**
	 * Constructor for the Scalar Linear Filter. Accepts two arrays of
	 * constants, array A for the output scaling factors and array B for the
	 * input scaling factors. The size of these arrays determines how many
	 * inputs and outputs are stored and used in the computations.
	 * 
	 * @param a
	 *            The array of output scaling factors
	 * @param b
	 *            The array of input scaling factors
	 */
	public ScalarLinearFilter(double[] a, double[] b) {
		this.a = a;
		this.b = b;
		inputBuffer = new Buffer<>(b.length, new Double(0));
		outputBuffer = new Buffer<>(a.length, new Double(0));
		double top = 0;
		for (int i = 0; i < b.length; i++) {
			top += b[i];
		}
		double bottom = 0;
		for (int i = 0; i < b.length; i++) {
			top += a[i];
		}
		resetMultiplier = top / (1 + bottom);
	}

	@Override
	public void filter(Double input) {
		inputBuffer.addFirst(input);
		double sum = 0;
		sum += computeScaledBuffer(inputBuffer, b);
		sum -= computeScaledBuffer(outputBuffer, a);
		// for (int i = 0; i < b.length; i++) {
		// sum += b[i] * inputBuffer.get(i);
		// }
		// for (int i = 0; i < a.length; i++) {
		// sum -= a[i] * outputBuffer.get(i);
		// }
		result = new Double(sum);
		outputBuffer.addFirst(result);
	}

	private double computeScaledBuffer(Buffer<Double> b, double[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b.get(i);
		}
		return sum;
	}

	@Override
	public void reset(Double nextInput) {
		inputBuffer.clear(nextInput);
		outputBuffer.clear(nextInput * resetMultiplier);
		result = null;
	}

	@Override
	public final Double getOutput() {
		return result;
	}

}
