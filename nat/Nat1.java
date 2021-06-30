
class base {
	interface Exp {
		Exp pred();
		boolean isZero();
	}

	static class Zero implements Exp {
		public Exp pred() { return this; }
		public boolean isZero() { return true; }
	}

	static class Succ implements Exp {
		private final Exp p;
		public Succ(Exp _p) { p = _p; }
		public Exp pred() { return p; }
		public boolean isZero() { return false; }
	}
}


class ext1 {
	interface Exp extends base.Exp {
		base.Exp add(base.Exp n);
	}

	static class Zero extends base.Zero implements Exp {
		public base.Exp add(base.Exp n) { return n; }	
	}

	static class Succ extends base.Succ implements Exp {
		public Succ(base.Exp p) { super(p); }
		public base.Exp add(base.Exp n) {
			return new Succ(pred().add(n));
		}
	}
}


class ext2 {
	interface Exp extends base.Exp {
		default base.Exp step() { return this; }
		default base.Exp eval() { return this; }
		default boolean canStep() { return false;  }
	}

	static class Zero extends base.Zero implements Exp {}
	static class Succ extends base.Succ implements Exp {
		public Succ(base.Exp p) { super(p); }
	}
	static class Add implements Exp {
		base.Exp l, r;
		
		public void setLeft(base.Exp _l) { l = _l; }
		public base.Exp getLeft() { return l; }
		public base.Exp getRight() { return r; }
		
		public Add(base.Exp _l, base.Exp _r)  { l = _l; r = _r; }

		public base.Exp pred() { return eval().pred(); }
		public boolean isZero() { return eval().isZero(); }

		public base.Exp step() {
			if (getLeft().canStep()) {
				setLeft(getLeft().step());
				return this;
			}
			if (getLeft().isZero()) { return getRight(); }

			return new Succ(new Add(getLeft().pred(), getRight()));	
		}
		public base.Exp eval() {
			Exp n = this;
			while (n.canStep()) { n = n.step(); }
			return n;
		}
		public boolean canStep() { return true; }
	}
}

class merged {
	interface Exp extends ext1.Exp, ext2.Exp {}

	static class Zero extends ext1.Zero /*, ext2.Zero */ implements Exp {}
	static class Succ extends ext1.Succ /* , ext2.Succ */ implements Exp {
		public Succ(base.Exp _p) {
			super(_p);
		}
	}
	static class Add extends ext2.Add implements Exp {
		public Add(base.Exp _l, base.Exp _r) {
			super(_l, _r);
		}
		public base.Exp add(base.Exp r) {
			return new Add(this, r);
		}
	}
}


public class Nat1 {
	public static void main(String [] args) {
		System.out.println("All clear!");
	}
}
