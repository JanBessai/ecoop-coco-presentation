// The expression problem, trivially! (Wang and Oliveira, Modularity 2016)

class base {
	interface Exp {
		Exp pred();
		boolean isZero();
	}

	interface Zero extends Exp {
		default Exp pred() { return this; }
		default boolean isZero() { return true; }
	}

	interface Succ extends Exp {
		public Exp getPred();
		default Exp pred() { return getPred(); }
		default boolean isZero() { return false; }
	}

	public static class finalized {
		public static class Zero implements base.Zero {}
		public static class Succ implements base.Succ {
			private final Exp p;
			public Succ(Exp _p) { p = _p; }
			public Exp getPred() { return p; }
		}
	}
}

class ext1 {
	interface Exp extends base.Exp {
		Exp pred();
		Exp add(Exp n);
	}

	interface Zero extends base.Zero, Exp {
		// duplicated
		default Exp pred() { return this; }
		default Exp add(Exp n) { return n; }	
	}

	interface Succ extends base.Succ, Exp {
		Exp getPred();
		// duplicated!
		default Exp pred() { return getPred(); }
		default Exp add(Exp n) {
			return new ext1.finalized.Succ(pred().add(n));
		}
	}

	public static class finalized {
		public static class Zero implements ext1.Zero {}
		public static class Succ implements ext1.Succ {
			private final Exp p;
			public Succ(Exp _p) { p = _p; }
			public Exp getPred() { return p; }
		}

	}
}


class ext2 {
	interface Exp extends base.Exp {
		Exp pred();
		default Exp step() { return this; }
		default Exp eval() { return this; }
		default boolean canStep() { return false;  }
	}

	interface Zero extends base.Zero, Exp {
		default Exp pred() { return this; }
	}
	interface Succ extends base.Succ, Exp {
		Exp getPred();
		default Exp pred() { return getPred(); }
	}
	interface Add extends Exp {
		Exp getLeft();
		Exp getRight();
		void setLeft(Exp _l);

		default Exp pred() { return eval().pred(); }
		default boolean isZero() { return eval().isZero(); }

		default Exp step() {
			if (getLeft().canStep()) {
				setLeft(getLeft().step());
				return this;
			}
			if (getLeft().isZero()) { return getRight(); }

			return new ext2.finalized.Succ(new ext2.finalized.Add(getLeft().pred(), getRight()));	
		}
		default Exp eval() {
			Exp n = this;
			while (n.canStep()) { n = n.step(); }
			return n;
		}
		default boolean canStep() { return true; }
	}
	
	public static class finalized {
		public static class Zero implements ext2.Zero {}
		public static class Succ implements ext2.Succ {
			private final Exp p;
			public Succ(Exp _p) { p = _p; }
			public Exp getPred() { return p; }
		}
		public static class Add implements ext2.Add {
			private Exp l, r;
			public Add(Exp _l, Exp _r) { l = _l; r = _r; }
			public Exp getLeft() { return l; }
			public Exp getRight() { return r; }
			public void setLeft(Exp _l) { l = _l; }
		}
	}
}

class merged {
	interface Exp extends ext1.Exp, ext2.Exp {
		Exp pred();
		// duplicated!
		default Exp eval() { return this; }
		// duplicated!
		default Exp step() { return this; }
		
		default Exp add(ext1.Exp r) {
			return new merged.finalized.Add(this, r);
		}
	}

	interface Zero extends ext1.Zero, ext2.Zero, Exp {
		// duplicated!
		default Exp pred() { return this; }

		// explicit disambiguation required
		default Exp add(ext1.Exp r) { return Exp.super.add(r); }
	}
	interface Succ extends ext1.Succ, ext2.Succ, Exp {
		Exp getPred();
		// duplicated!
		default Exp pred() { return getPred(); }

		// explicit disambiguation required
		default Exp add(ext1.Exp r) { return Exp.super.add(r); }
	}
	interface Add extends ext2.Add, Exp {
		Exp getLeft();
		Exp getRight();

		// duplicated!
		default Exp pred() { return eval().pred(); }
		// duplicated!
		default Exp eval() {
			Exp n = this;
			while (n.canStep()) { n = n.step(); }
			return n;
		}	
		// duplicated
		default Exp step() {
			if (getLeft().canStep()) {
				setLeft(getLeft().step());
				return this;
			}
			if (getLeft().isZero()) { return getRight(); }

			// changed in a subtle way! merged vs ext2
			return new merged.finalized.Succ(
				new merged.finalized.Add(getLeft().pred(),
							getRight()));	
		}

	}

	public static class finalized {
		public static class Zero implements merged.Zero {}
		public static class Succ implements merged.Succ {
			private final Exp p;
			public Succ(Exp _p) { p = _p; }
			public Exp getPred() { return p; }
		}
		public static class Add implements merged.Add {
			private Exp l, r;
			public Add(Exp _l, Exp _r) { l = _l; r = _r; }
			public Exp getLeft() { return l; }
			public Exp getRight() { return r; }
			public void setLeft(Exp _l) { l = _l; }
		}
	}
}


public class Nat2 {
	public static void main(String [] args) {
		System.out.println("All clear!");
	}
}
