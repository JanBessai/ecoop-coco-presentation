class Base {
	interface List {
		Object getHead();
		List getTail();
	}

	public static class Nil {
		public Object getHead() { return null; }
		public List getTail() { return null; }
	}

	public static class Cons {
		private final Object hd;
		private final List tl;
		public Cons(Object _hd, List _tl) { hd = _hd; tl = _tl; }

		public Object getHead() { return hd; }
		public List getTail() { return tl; }
	}
}






















//*
class Ext1 {
	interface List extends Base.List {
		List append(List r);
	}

	public static abstract class Nil extends Base.Nil implements List {
		public List append(List r) {
			return null; // TODO
		}
	}

	public static abstract class Cons extends Base.Cons implements List {
		public Cons(Object _hd, List _tl) { super(_hd, _tl); }
		public List append(List r) {
			return null; // TODO
		}
	}

}
//*/





















/*
class Ext2 {
	interface Fun { List apply(Object o); }
	
	interface List extends Ext2.List {
		List flatMap(Fun f);
	}

	public static abstract class Nil extends Ext2.Nil implements List {
		public List flatMap(Fun f) {
			return null; // TODO
		}
	}

	public static abstract class Cons extends Ext2.Cons implements List {
		public Cons(Object _hd, List _tl) { super(_hd, _tl); }
		public List flatMap(Fun f) {
			return null; // TODO
		}
	}
}
//*/




















/*
class Ext3 {
	public abstract class Append extends List {
		private List l, r;
		public Append(List _l, List _r) { l = _l; r = _r; }
		public List getLeft() { return l; }
		public List getRight() { return r; }
		public void setLeft(List _l) { l = _l; }
		public void setRight(List _r) { r = _r; }
		public List hnf() {
			List eval = this;
			while (eval.canStep()) { eval = eval.step(); }
			return eval;
		}
		public List step() {
			if (getLeft().canStep()) {
				setLeft(getLeft().step());
				return this;
			}
			return getLeft().delayAppend(r);
		}
		public boolean canStep() { return true; }
		
		public List delayAppend(List o) {
			return app(getLeft(), app(getRight(), o));
		}
		
		public Object getHead() { return hnf().getHead(); }
		public List getTail() { return hnd().getTail(); }
		public List flatMap() { 
			return app(getLeft().flatMap(f), getRight().flatMap(f)); 
		}
	}

	interface Factory extends Ext2.Factory {
		List app(List l, List r);
		List convert(Base.List l);
	}

	interface List extends Ext2.List, Factory {
		default List step() { return this; }
		default boolean canStep() { return false; }
		default List hnf() { return this; }
		List delayedAppend(List o);
	}

	public static abstract class Nil extends Ext2.Nil implements List {
		public List delayedAppend(List o) { return o; } 
	}

	public static abstract class Cons extends Ext2.Cons implements List {
		public Cons(Object _hd, List _tl) { super(_hd, _tl); }
		public List delayedAppend(List o) {
			return null; // TODO
		}
	}
}
//*/






















/*
class BaseFinal {
	interface Factory { // TODO
		public Base.List nil() { return new Nil(); }
		public Base.List cons() { // TODO
			// TODO
		}
		public Base.List convert(Base.List l) { 
			// TODO
		}
	}
	interface List { // TODO
	}
	class Nil {} // TODO
	class Cons {
		private final Object _hd;
		private final List _tl
		public Cons(Object _hd, List _tl) { hd = _hd; tl = _tl; }
	}
}
//*/

/*
class Ext1Final {
	// TODO
} 
//*/

/*
class Ext2Final {
	// TODO
}
//*/
















/*
class Ext3Final {
	// TODO
	public class Append extends Ext3.List {
		private List l, r;
		public Append(List _l, List _r) { l = _l; r = _r; }
		public List getLeft() { return l; }
		public List getRight() { return r; }
		public void setLeft(List _l) { l = _l; }
		public void setRight(List _r) { r = _r; }
	}
}
//*/





























/*
class Client2<T> {
	private final Ext2.Factory<T> f;
	public Client2<T>(Ext2.Factory<T> _f) { f = _f; }

	public static void test() {
		Base.List r = cons("two", cons("three", nil()));
		Base.List l = cons("one", nil());
		List appended = f.convert(l).append(r);
		List duplicated = 
			f.convert(appended).flatMap(o -> 
					f.cons(o, f.cons(o, f.nil()))
			); 
		Base.List iter = diplicated;
		while (iter.getHead() != null) {
			System.out.println(iter.getHead());
			iter = iter.getTail();
		}
	}
}
//*/

// Dummy
class ECOOP { 
	public static void main(String args[]) {
		System.out.println("All clear!");

		/*
		Client2<ExtFinal.List> c2 = 
			new Client2<>(new Ext2Final.Factory {});
		c2.test();

		Client2<ExtFinal.List> c3 = 
			new Client2<>(new Ext3Final.Factory {});
		c3.test();
		//*/
	}
}
