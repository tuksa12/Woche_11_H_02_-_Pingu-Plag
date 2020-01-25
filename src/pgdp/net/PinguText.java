package pgdp.net;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PinguText {

	private static final int NGRAM_SIZE = 3;

	private final long id;
	private final String title;
	private final String author;
	private final String text;
	private final List<String> words;
	private final Map<Ngram, Integer> ngrams;

	public PinguText(long id, String title, String author, String text) {
		this.id = id;
		this.title = Objects.requireNonNull(title);
		this.author = Objects.requireNonNull(author);
		this.text = Objects.requireNonNull(text);
		this.words = List.of(text.split("[^\\p{Alnum}]+"));
		Map<Ngram, Integer> tempNgrams = new HashMap<>();
		for (int i = 0; i < words.size() - NGRAM_SIZE; i++) {
			tempNgrams.merge(new Ngram(i, i + NGRAM_SIZE), 1, Integer::sum);
		}
		this.ngrams = Collections.unmodifiableMap(tempNgrams);
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getText() {
		return text;
	}

	public List<String> getWords() {
		return words;
	}

	public Map<Ngram, Integer> getNgrams() {
		return ngrams;
	}

	public double computeSimilarity(PinguText other) {
		Map<Ngram, Integer> intersection = new HashMap<>(ngrams);
		intersection.replaceAll((ngram, count) -> Math.min(count, other.ngrams.getOrDefault(ngram, 0)));
		return intersection.values().stream().mapToInt(Integer::intValue).sum() * 2.0
				/ (getNgramCount() + other.getNgramCount());
	}

	private int getNgramCount() {
		return Math.max(words.size() - NGRAM_SIZE, 0);
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PinguText))
			return false;
		PinguText other = (PinguText) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return String.format("%s by %s", title, author);
	}

	class Ngram {
		private final int fromIndex;
		private final int toIndex;
		private final int hashCode;

		Ngram(int fromIndex, int toIndex) {
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			Objects.checkFromToIndex(fromIndex, toIndex, words.size());
			int hash = 1;
			for (int i = fromIndex; i < toIndex; i++)
				hash = hash * 31 + words.get(i).hashCode();
			hashCode = hash;
		}

		List<String> getWords() {
			return words;
		}

		int size() {
			return toIndex - fromIndex;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!(obj instanceof Ngram))
				return false;
			Ngram other = (Ngram) obj;
			if (hashCode != other.hashCode || size() != other.size())
				return false;
			for (int i = fromIndex, j = other.fromIndex; i < toIndex; i++, j++)
				if (!Objects.equals(words.get(i), other.getWords().get(j)))
					return false;
			return true;
		}

		@Override
		public String toString() {
			return String.join(" ", words.subList(fromIndex, toIndex));
		}
	}
}
