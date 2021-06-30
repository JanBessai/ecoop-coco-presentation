class base {
	interface Factory<T> {
		Exp<T> zero();
		Exp<T> succ(Exp<T> p);
		Exp<T> convert(Exp<T> o);
	}

	interface Exp<T> extends Factory<T> {
		T getSelf();

		Exp<T> pred();
		boolean isZero();
	}

	interface Zero<T> extends Exp<T> {
		default Exp<T> pred() { return this; }
		default boolean isZero() { return true; }
	}

	interface Succ<T> extends Exp<T> {
		public Exp<T> getPred();
		default Exp<T> pred() { return getPred(); }
		default boolean isZero() { return false; }
	}

	public static class finalized {
		interface Factory extends base.Factory<Exp> {
			default base.Exp<Exp> zero() { return new Zero(); }
			default base.Exp<Exp> succ(base.Exp<Exp> p) {
				return new Succ(p);
			}
			default base.Exp<Exp> convert(base.Exp<Exp> o) {
				return o.getSelf();
			}
		}

		interface Exp extends base.Exp<Exp>, Factory {
			default Exp getSelf() { return this; }
		}

		public static class Zero implements base.Zero<Exp>, Exp {}
		public static class Succ implements base.Succ<Exp>, Exp {
			private final base.Exp<Exp> p;
			public Succ(base.Exp<Exp> _p) { p = _p; }
			public base.Exp<Exp> getPred() { return p; }
		}
	}
}

class ext1 {
	interface Factory<T> extends base.Factory<T> {
		Exp<T> convert(base.Exp<T> o);
	}

	interface Exp<T> extends base.Exp<T>, Factory<T> {
		base.Exp<T> add(base.Exp<T> n);
	}

	interface Zero<T> extends base.Zero<T>, Exp<T> {
		default base.Exp<T> add(base.Exp<T> n) { return n; }	
	}

	interface Succ<T> extends base.Succ<T>, Exp<T> {
		default base.Exp<T> add(base.Exp<T> n) {
			return succ(convert(pred()).add(n));
		}
	}

	public static class finalized {
		interface Factory extends ext1.Factory<Exp> {
			default base.Exp<Exp> zero() { return new Zero(); }
			default base.Exp<Exp> succ(base.Exp<Exp> p) {
				return new Succ(p);
			}
			default ext1.Exp<Exp> convert(base.Exp<Exp> o) {
				return o.getSelf();
			}
		}

		interface Exp extends ext1.Exp<Exp>, Factory {
			default Exp getSelf() { return this; }
		}

		public static class Zero implements ext1.Zero<Exp>, Exp {}
		public static class Succ implements ext1.Succ<Exp>, Exp {
			private final base.Exp<Exp> p;
			public Succ(base.Exp<Exp> _p) { p = _p; }
			public base.Exp<Exp> getPred() { return p; }
		}
	}
}


class ext2 {
	interface Factory<T> extends base.Factory<T> {
		base.Exp<T> add(base.Exp<T> l, base.Exp<T> r);
		Exp<T> convert(base.Exp<T> o);
	}

	interface Exp<T> extends base.Exp<T>, Factory<T> {
		default base.Exp<T> step() { return this; }
		default base.Exp<T> eval() { return this; }
		default boolean canStep() { return false;  }
	}

	interface Zero<T> extends base.Zero<T>, Exp<T> {}
	interface Succ<T> extends base.Succ<T>, Exp<T> {}
	interface Add<T> extends Exp<T> {
		base.Exp<T> getLeft();
		base.Exp<T> getRight();
		void setLeft(base.Exp<T> _l);

		default base.Exp<T> pred() { return eval().pred(); }
		default boolean isZero() { return eval().isZero(); }

		default base.Exp<T> step() {
			Exp<T> l = convert(getLeft());
			if (l.canStep()) {
				setLeft(l.step());
				return this;
			}
			if (l.isZero()) { return getRight(); }

			return succ(add(l.pred(), getRight()));	
		}
		default base.Exp<T> eval() {
			Exp<T> n = this;
			while (n.canStep()) { n = convert(n.step()); }
			return n;
		}
		default boolean canStep() { return true; }
	}
	
	public static class finalized {
		interface Factory extends ext2.Factory<Exp> {
			default base.Exp<Exp> zero() { return new Zero(); }
			default base.Exp<Exp> succ(base.Exp<Exp> p) {
				return new Succ(p);
			}
			default base.Exp<Exp> add(base.Exp<Exp> l, base.Exp<Exp> r) {
				return new Add(l, r);
			}
			default ext2.Exp<Exp> convert(base.Exp<Exp> o) {
				return o.getSelf();
			}
		}

		interface Exp extends ext2.Exp<Exp>, Factory {
			default Exp getSelf() { return this; }
		}

		public static class Zero implements ext2.Zero<Exp>, Exp {}
		public static class Succ implements ext2.Succ<Exp>, Exp {
			private final base.Exp<Exp> p;
			public Succ(base.Exp<Exp> _p) { p = _p; }
			public base.Exp<Exp> getPred() { return p; }
		}
		public static class Add implements ext2.Add<Exp>, Exp {
			private base.Exp<Exp> l, r;
			public Add(base.Exp<Exp> _l, base.Exp<Exp> _r) { l = _l; r = _r; }
			public base.Exp<Exp> getLeft() { return l; }
			public base.Exp<Exp> getRight() { return r; }
			public void setLeft(base.Exp<Exp> _l) { l = _l; }
		}
	}
}

class merged {
	interface Factory<T> extends ext1.Factory<T>, ext2.Factory<T> {
		Exp<T> convert(base.Exp<T> o);
	}

	interface Exp<T> extends ext1.Exp<T>, ext2.Exp<T>, Factory<T> {
		default base.Exp<T> add(base.Exp<T> r) {
			return add(this, r);
		}
	}

	interface Zero<T> extends ext1.Zero<T>, ext2.Zero<T>, Exp<T> {
		// explicit disambiguation required
		default base.Exp<T> add(base.Exp<T> r) { return Exp.super.add(r); }
	}
	interface Succ<T> extends ext1.Succ<T>, ext2.Succ<T>, Exp<T> {
		// explicit disambiguation required
		default base.Exp<T> add(base.Exp<T> r) { return Exp.super.add(r); }
	}
	interface Add<T> extends ext2.Add<T>, Exp<T> {
	}

	public static class finalized {
		interface Factory extends merged.Factory<Exp> {
			default base.Exp<Exp> zero() { return new Zero(); }
			default base.Exp<Exp> succ(base.Exp<Exp> p) {
				return new Succ(p);
			}
			default base.Exp<Exp> add(base.Exp<Exp> l, base.Exp<Exp> r) {
				return new Add(l, r);
			}
			default merged.Exp<Exp> convert(base.Exp<Exp> o) {
				return o.getSelf();
			}
		}

		interface Exp extends merged.Exp<Exp>, Factory {
			default Exp getSelf() { return this; }
		}

		public static class Zero implements merged.Zero<Exp>, Exp {}
		public static class Succ implements merged.Succ<Exp>, Exp {
			private final base.Exp<Exp> p;
			public Succ(base.Exp<Exp> _p) { p = _p; }
			public base.Exp<Exp> getPred() { return p; }
		}
		public static class Add implements merged.Add<Exp>, Exp {
			private base.Exp<Exp> l, r;
			public Add(base.Exp<Exp> _l, base.Exp<Exp> _r) { l = _l; r = _r; }
			public base.Exp<Exp> getLeft() { return l; }
			public base.Exp<Exp> getRight() { return r; }
			public void setLeft(base.Exp<Exp> _l) { l = _l; }
		}
	}
}

class Client<T> {
	final ext1.Factory<T> f;

	public Client(ext1.Factory<T> _f) { f = _f; }

	public void test() {
		base.Exp<T> one = f.succ(f.zero());
		base.Exp<T> two = f.convert(one).add(one);
		System.out.println(String.format("two.isZero(): %b", two.isZero()));
		System.out.println(String.format("two.pred().isZero(): %b", two.pred().isZero()));
		System.out.println(String.format("two.pred().pred().isZero(): %b", two.pred().pred().isZero()));
	}
}


public class Nat3 {
	public static void main(String [] args) {
		System.out.println("All clear!");
		
		Client<ext1.finalized.Exp> c1 = 
			new Client<>(new ext1.finalized.Factory() {});
		c1.test();
		
		Client<merged.finalized.Exp> cMerged = 
			new Client<>(new merged.finalized.Factory() {});
		cMerged.test();
	}
}
