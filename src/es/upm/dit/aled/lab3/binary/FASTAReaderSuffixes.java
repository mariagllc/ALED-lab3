package es.upm.dit.aled.lab3.binary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.upm.dit.aled.lab3.FASTAReader;

/**
 * Reads a FASTA file containing genetic information and allows for the search
 * of specific patterns within these data. The information is stored as an array
 * of bytes that contain nucleotides in the FASTA format. Since this array is
 * usually created before knowing how many characters in the origin FASTA file
 * are valid, an int indicating how many bytes of the array are valid is also
 * stored. All valid characters will be at the beginning of the array.
 * 
 * This extension of the FASTAReader uses a sorted dictionary of suffixes to
 * allow for the implementation of binary search.
 * 
 * @author mmiguel, rgarciacarmona
 *
 */
public class FASTAReaderSuffixes extends FASTAReader {
	protected Suffix[] suffixes;

	/**
	 * Creates a new FASTAReader from a FASTA file.
	 * 
	 * At the end of the constructor, the data is sorted through an array of
	 * suffixes.
	 * 
	 * @param fileName The name of the FASTA file.
	 */
	public FASTAReaderSuffixes(String fileName) {
		// Calls the parent constructor
		super(fileName);
		this.suffixes = new Suffix[validBytes];
		for (int i = 0; i < validBytes; i++)
			suffixes[i] = new Suffix(i);
		// Sorts the data
		sort();
	}

	/*
	 * Helper method that creates a array of integers that contains the positions of
	 * all suffixes, sorted alphabetically by the suffix.
	 */
	private void sort() {
		// Instantiate the external SuffixComparator, passing 'this' (the reader)
		// so it can access the content and validBytes fields.
		SuffixComparator suffixComparator = new SuffixComparator(this);
		// Use the external Comparator for sorting.
		Arrays.sort(this.suffixes, suffixComparator);
	}

	/**
	 * Prints a list of all the suffixes and their position in the data array.
	 */
	public void printSuffixes() {
		System.out.println("-------------------------------------------------------------------------");
		System.out.println("Index | Sequence");
		System.out.println("-------------------------------------------------------------------------");
		for (int i = 0; i < suffixes.length; i++) {
			int index = suffixes[i].suffixIndex;
			String ith = "\"" + new String(content, index, Math.min(50, validBytes - index)) + "\"";
			System.out.printf("  %3d | %s\n", index, ith);
		}
		System.out.println("-------------------------------------------------------------------------");
	}

	/**
	 * Implements a binary search to look for the provided pattern in the data
	 * array. Returns a List of Integers that point to the initial positions of all
	 * the occurrences of the pattern in the data.
	 * 
	 * @param pattern The pattern to be found.
	 * @return All the positions of the first character of every occurrence of the
	 *         pattern in the data.
	 */
	@Override
	public List<Integer> search(byte[] pattern) {
		// TODO
		int lo = 0, hi = suffixes.length - 1;
		boolean found = false;

		List<Integer> resultado = new ArrayList<>();

		while (lo <= hi && !found) { // que tdv no se haya encontrado, q found siga siendo false
			int index = 0; // rastrea el carácter actual que se está comparando con el pattern
			int m = (int) Math.floor(lo + (hi - lo) / 2); // índice medio para cada iteración
			int posSuffix = suffixes[m].getIndex(); // tuve que definir el método getIndex en la clase Suffix yo misma,
													// para que no diese error, no se podía hacer la conversión directa
			while (index < pattern.length && (posSuffix + index) < validBytes
					&& pattern[index] == content[posSuffix + index]) {
				index++;
			}
			if (index == pattern.length /* && suffixes[m].charAt(index) == null */) { // el ínidce ya ha terminado los
																						// elementos del pattern, solo
																						// queda ver que en el suffix no
																						// hay tmp más elementos, q solo
																						// este CATANA ("y los últimos
																						// caracteres también
																						// coinciden")
				// Coincidencia completa encontrada
				resultado.add(posSuffix);
				int posResult = posSuffix;
				found = true;
			} else if (pattern[index] < content[posSuffix + index]) { // || (posSuffix + index) >= validBytes
				hi = m--;
				index = 0;
			} else if (pattern[index] > content[posSuffix + index]) {
				lo = m++;
				index = 0;
			}
		}
		
		for (int i = resultado.get(0) + 1; i < validBytes; i++) { // +1 para no volver a guardar la posicion que
																	// encontré on la primera busqueda, apartado 4
			if (pattern[0] == content[i])
				resultado.add(i);
		}

		for (int j = resultado.get(0) - 1; j >= 0; j--) { // -1 para no volver a guardar la posicion que encontré on la
															// primera busqueda, apartado 4
			if (pattern[0] == content[j])
				resultado.add(j);
		}
		return resultado;
	}

	public static void main(String[] args) {
		long t1 = System.nanoTime();
		FASTAReaderSuffixes reader = new FASTAReaderSuffixes(args[0]);
		if (args.length == 1)
			return;
		byte[] patron = args[1].getBytes();
		System.out.println("Tiempo de apertura de fichero: " + (System.nanoTime() - t1));
		long t2 = System.nanoTime();
		System.out.println("Tiempo de ordenación: " + (System.nanoTime() - t2));
		reader.printSuffixes();
		long t3 = System.nanoTime();
		List<Integer> posiciones = reader.search(patron);
		System.out.println("Tiempo de búsqueda: " + (System.nanoTime() - t3));
		if (posiciones.size() > 0) {
			for (Integer pos : posiciones)
				System.out.println("Encontrado " + args[1] + " en " + pos);
		} else
			System.out.println("No he encontrado " + args[1] + " en ningún sitio.");
		System.out.println("Tiempo total: " + (System.nanoTime() - t1));
	}
}
