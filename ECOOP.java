class Base {

	interface Factory<T> {
		List<T> nil();
		List<T> cons(Object hd, List<T> tl);
		List<T> convert(List<T> l);
	}

	interface List<T> {
		Object getHead();
		List<T> getTail();
		T getSelf();
	}

	interface Nil<T> extends List<T> {
		default Object getHead() { return null; }
		default List<T> getTail() { return null; }
	}

        interface Cons<T> extends List<T> {
		public Object getHead();
		public List getTail();
	}
}


class Ext1 {
	
	interface Factory<T> extends Base.Factory<T> {
		List<T> convert(Base.List<T> l);
	}

	interface List<T> extends Base.List<T>, Factory<T> {
		List<T> append(List<T> r);
	}

	interface Nil<T> extends Base.Nil<T>, List<T> {
		default List<T> append(List<T> r) {
			return r;
		}
	}

	interface Cons<T> extends Base.Cons<T>, List<T> {
		default List<T> append(List<T> r) {
			return convert(cons(getHead(), convert(getTail()).append(r)));
		}
	}

}
//*/


class Ext2 {
	interface Fun<T> { List<T> apply(Object o); }

	interface Factory<T> extends Ext1.Factory<T> {
		List<T> convert(Base.List<T> l);
	}
	
	interface List<T> extends Ext1.List<T>, Factory<T> {
		List<T> flatMap(Fun<T> f);
	}

	interface Nil<T> extends Ext1.Nil<T>, List<T> {
		default List<T> flatMap(Fun<T> f) {
			return convert(nil());
		}
	}

	interface Cons<T> extends Ext1.Cons<T>,  List<T> {
		default List flatMap(Fun<T> f) {
			return convert(f.apply(getHead()).append(convert(getTail()).flatMap(f)));
		}
	}
}
//*/

/*
class Ext3 {
	public abstract class Append implements List {
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
		public Base.List getTail() { return hnf().getTail(); }
		public Ext2.List flatMap(Ext2.Fun f) { 
			return app(convert(getLeft().flatMap(f)),
					convert(getRight().flatMap(f))); 
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
		List delayAppend(List o);
	}

	public static abstract class Nil extends Ext2.Nil implements List {
		public List delayAppend(List o) { return o; } 
	}

	public static abstract class Cons extends Ext2.Cons implements List {
		public Cons(Object _hd, List _tl) { super(_hd, _tl); }
		public List delayAppend(List o) {
			return convert(cons(getHead(), app(convert(getTail()), o)));
		}
	}
}
//*/


class BaseFinal {
	interface Factory extends Base.Factory<List> {
		default Base.List nil() { return new Nil(); }
		default Base.List cons(Object hd, List tl) {
			return new Cons(hd, tl);
		}
		default Base.List<List> convert(Base.List<List> l) { 
			return l.getSelf();
		}
	}
	interface List extends Base.List<List> {
		default List getSelf() { return this; }
	}
	static class Nil implements List, Base.Nil<List> {}
	static class Cons implements List, Base.Cons<List> {
		private final Object hd;
		private final List tl;
		public Cons(Object _hd, List _tl) { hd = _hd; tl = _tl; }
		public Object getHead() { return hd; }
		public List getTail() { return tl; }
	}
}
//*/


class Ext1Final {
	interface Factory extends Ext1.Factory<List> {
		default Base.List<List> nil() { return new Nil(); }
		default Base.List<List> cons(Object hd, Base.List tl) {
			return new Cons(hd, convert(tl));
		}
		default List convert(Base.List<List> l) { 
			return l.getSelf();
		}
	}
	interface List extends Ext1.List<List>, Factory {
		default List getSelf() { return this; }
	}
	static class Nil implements List, Ext1.Nil<List> {}
	static class Cons implements List, Ext1.Cons<List> {
		private final Object hd;
		private final List tl;
		public Cons(Object _hd, List _tl) { hd = _hd; tl = _tl; }
		public Object getHead() { return hd; }
		public List getTail() { return tl; }
	}
} 
//*/

///*
class Ext2Final {
	interface Factory extends Ext2.Factory<List> {
		default Base.List<List> nil() { return new Nil(); }
		default Base.List<List> cons(Object hd, Base.List tl) {
			return new Cons(hd, convert(tl));
		}
		default List app(Ext2.List<List> l, Ext2.List<List> r) {
			return new Append(l, r);
		}
		default List convert(Base.List<List> l) { 
			return l.getSelf();
		}
	}
	interface List extends Ext2.List<List>, Factory {
		default List getSelf() { return this; }
	}
	static class Nil implements List, Ext1.Nil<List> {}
	static class Cons implements List, Ext1.Cons<List> {
		private final Object hd;
		private final List tl;
		public Cons(Object _hd, List _tl) { hd = _hd; tl = _tl; }
		public Object getHead() { return hd; }
		public List getTail() { return tl; }
	}
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
