class List {
	public static void main(String [] args) {
		Base.test();
	}
}

class Base {
	public interface List {
		public Object getHead();
		public List getTail();
	}
	public static class Nil implements List {
		public Object getHead() { return null; }
		public List getTail() { return null; }
	}
	public static class Cons implements List {
		private final Object hd;
		private final List tl;
		public Cons(Object _hd, List _tl) { hd = _hd; tl = _tl; };
		public Object getHead() { return hd; }
		public List getTail() { return tl; }
	}

	public static void test() {
		List end = new Nil();
		List two = new Cons("two", end);
		List one = new Cons("one", two);
		System.out.println(end.getHead() == null);
		System.out.println(end.getTail() == null);
		System.out.println(two.getHead().equals("two"));
		System.out.println(two.getTail() == end);
		System.out.println(one.getHead().equals("one"));
		System.out.println(one.getTail() == two);
	}
}

/* To show failure /
class Ext1 {
	public interface List extends Base.List {
		public List append(List r);
	}
	public static class Nil extends Base.Nil implements List {
		public List append(List r) { return r; }
	}
	public static class Cons extends Base.Cons implements List {
		public Cons(Object hd, List tl) { super(hd, tl); }
		public List append(List r) {
			return new Cons(getHead(), getTail().append(r));
		}
	}
}
//*/

/* To show need for Factory /
class Ext1 {
	public interface List extends Base.List {
		public List append(List r);
		public List convert(Base.List l);
	}
	public static abstract class Nil extends Base.Nil implements List {
		public List append(List r) { return r; }
	}
	public static abstract class Cons extends Base.Cons implements List {
		public Cons(Object hd, List tl) { super(hd, tl); }
		public List append(List r) {
			return new Cons(getHead(), convert(getTail()).append(r));
		}
	}
}
//*/

/* Version with Factory */
class Ext1 {
	public interface Factory {
		public List nil();
		public List cons(Object hd, List tl);
	}
	public interface List extends Base.List, Factory {
		public List append(List r);
		public List convert(Base.List l);
	}
	public static abstract class Nil extends Base.Nil implements List {
		public List append(List r) { return r; }
	}
	public static abstract class Cons extends Base.Cons implements List {
		public Cons(Object hd, List tl) { super(hd, tl); }
		public List append(List r) {
			return cons(getHead(), convert(getTail()).append(r));
		}
	}
}
//*/

/* Failures: conversion required /
class Ext2 {
	public interface Fun { public List apply(Object o); }
	public interface List extends Ext1.List {
		public List flatMap(Fun f);
	}
	public static abstract class Nil extends Ext1.Nil implements List {
		public List flatMap(Fun f) { return nil(); }
	}
	public static abstract class Cons extends Ext1.Cons implements List {
		public Cons(Object hd, List tl) { super(hd, tl); }
		public List flatMap(Fun f) {
			List l = f.apply(getHead());
			List r = getTail().flatMap(f);
			return l.append(r);
		}
	}
}
*/

class Ext2 {
	public interface Fun { public List apply(Object o); }
	public interface List extends Ext1.List {
		public List flatMap(Fun f);
		public List convert(Base.List l);
	}
	public static abstract class Nil extends Ext1.Nil implements List {
		public List flatMap(Fun f) { return convert(nil()); }
	}
	public static abstract class Cons extends Ext1.Cons implements List {
		public Cons(Object hd, List tl) { super(hd, tl); }
		public List flatMap(Fun f) {
			List l = f.apply(getHead());
			List r = convert(getTail()).flatMap(f);
			return convert(l.append(r));
		}
	}
}

/* Shows how to add a data type with new methods */
class Ext3 {
	public abstract class Append implements List {
		private List l, r;
		public Append(List _l, List _r) { l = _l; r = _r; }
		public List delayAppend(List toAppend) {
			return append(l, append(r, toAppend));
		}
		public boolean canStep() { return true;	}
		public List step() {
			if (l.canStep()) {
				l = l.step();
				return this;
			} else {
				return l.delayAppend(r);
			}
		}
			
		public List hnf() {
			List eval = this;
			while (eval.canStep()) { eval = eval.step(); }
			return eval;
		}
		public Object getHead() { return hnf().getHead(); }
		public Base.List getTail() { return hnf().getTail(); }
		public List flatMap(Ext2.Fun f) { 
			return append(convert(l.flatMap(f)), 
					convert(r.flatMap(f)));
		}
	}

	public interface Factory extends Ext1.Factory {
		public List append(List l, List r);
	}

	public interface List extends Ext2.List, Factory {
		public List delayAppend(List toAppend);
		public default boolean canStep() { return false; }
		public default List step() { return this; }
		public default List hnf() { return this; }
		public default List append(List r) { return append(this, r); }
		public List convert(Base.List l);
	}

	public abstract class Nil extends Ext2.Nil implements List {
		public List delayAppend(List toAppend) { return toAppend; }
	}

	public abstract class Cons extends Ext2.Cons implements List {
		public Cons(Object hd, List tl) { super(hd, tl); }
		public List delayAppend(List toAppend) {
			return convert(cons(getHead(), append(convert(getTail()), toAppend)));
		}
	}
}
//*/


