import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/* WATOR predator-prey simulation based on A.K. Dewdney "Sharks and fish
   wage an ecological war on the toroidal planet Wa-Tor" (Scientific
   American, December 1984).  Source code recovered from my 1996 compiled
   applet with help from JDK 1.2 disassembler.  This version compiled
   with JDK 1.1.7A and exactly matches original's disassembler output.
   Placed in the public domain.  This product is supplied "as is."
   Lawrence Leinweber, Cleveland, Ohio, U.S.A. 1999. */
// New and improved for 2015, now with delay, more O-O, less memory mgmt ops

public class wator extends Applet 
implements Runnable, ActionListener {

private static final long serialVersionUID = 42L;

slider sl_ncell;
slider sl_nshark;
slider sl_nfish;
slider sl_sharkbreed;
slider sl_fishbreed;
slider sl_sharkstarve;
slider sl_delay;
button bn_go;
button bn_stop;
button bn_remix;
Panel pn_tp;
Panel pn_sc;
Panel pn_bn;
panelmap pn_mp;
panelgrf pn_gf;
int nrow;
int ncol;
int ncell;
int nsizemax;
int nsize;
int nshark;
int nfish;
int delay;
boolean goflag;
boolean remixflag;
boolean brkflag;
boolean runflag;
Object mutex;
private Thread thread;
menagerie menagerie;
species ocean, fish, shark;

public void start()
{
	synchronized (mutex) {
		if (thread != null) return;
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		runflag = true;
	}
}

public void stop()
{
	runflag = false;
}

public void run()
{
	int i, j, s, f;
	
	while (runflag && (remixflag || pn_mp.paintsyncflag || brkflag || goflag)) {
		try {
			Thread.sleep(delay);
		}
		catch (Exception e) {}
		if (remixflag) {
			synchronized (mutex)
				{ i = nrow; j = ncol; s = nshark; f = nfish; }
			pn_mp.remix(i, j);
			if (fish.cnt != f || shark.cnt != s)
				pn_mp.remake(f, s);
			pn_mp.paintsyncflag = true;
			pn_gf.brk();
			remixflag = false;
		}
		pn_mp.paintupdate();
		if (brkflag)
			{ brkflag = false; pn_gf.brk(); }
		if (goflag) {
			pn_mp.gen();
			pn_gf.gen(ncell, fish.cnt, shark.cnt);
			synchronized (mutex) {
				if (!remixflag) {
					setnfish(fish.cnt);
					setnshark(shark.cnt);
				}
			}
		}
	}
	thread = null;
}

public void actionPerformed(ActionEvent e) {
	button button;
	slider slider;
	int value;
	
	if (e.getID() != Event.ACTION_EVENT) return;
	if (e.getSource() instanceof Button) {
			button = (button) e.getSource();
			if (button == bn_go) goflag = true;
			if (button == bn_stop) goflag = false;
			if (button == bn_remix) remixflag = true;
			if (goflag || remixflag) start();
		}
	if (e.getSource() instanceof slider) {
	    synchronized (mutex) {
	    	slider = (slider) e.getSource();
	    	value = slider.scrollbar.value;
			if (slider == sl_ncell)
				remixflag = setsize(value);
			if (slider == sl_nshark)
				remixflag = setnshark(value);
			if (slider == sl_nfish)
				remixflag = setnfish(value);
			if (slider == sl_sharkbreed)
				brkflag = setsbreed(value);
			if (slider == sl_fishbreed)
				brkflag = setfbreed(value);
			if (slider == sl_sharkstarve)
				brkflag = setstarve(value);
			if (slider == sl_delay)
				brkflag = setdelay(value);
		    if (remixflag) start();
		}
	}
}

public void init()
{
	super.init();
	menagerie = new menagerie();
	ocean = menagerie.ocean;
	fish = menagerie.fish;
	shark = menagerie.shark;
	pn_mp = new panelmap(menagerie);
	pn_mp.applet = this;
	setLayout(new GridLayout(2, 1, 10, 10));
	pn_tp = new Panel();
	pn_tp.setLayout(new BorderLayout(3, 0));
	add(pn_tp);
	pn_tp.setBackground(menagerie.backgroundcolor);
	pn_sc = new Panel();
	pn_sc.setLayout(new GridLayout(16, 1, 3, 3));
	pn_tp.add("East", pn_sc);
	sl_ncell = new slider("Size", ocean.color, pn_sc, this);
	sl_nshark = new slider("Sharks", shark.color, pn_sc, this);
	sl_nfish = new slider("Fish", fish.color, pn_sc, this);
	sl_sharkbreed = new slider("Shark Breed", shark.color, pn_sc, this);
	sl_fishbreed = new slider("Fish Breed", fish.color, pn_sc, this);
	sl_sharkstarve = new slider("Shark Starve", shark.color, pn_sc, this);
	sl_delay = new slider("Delay", menagerie.foregroundcolor, pn_sc, this);
	pn_sc.add(new Label());
	pn_bn = new Panel();
	pn_bn.setLayout(new GridLayout(1, 3, 3, 0));
	pn_sc.add(pn_bn);
	bn_go = new button("Go", pn_bn, this);
	bn_stop = new button("Stop", pn_bn, this);
	bn_remix = new button("Remix", pn_bn, this);
	pn_tp.add("Center", pn_mp);
	pn_gf = new panelgrf();
	add(pn_gf);
	validate();
	pn_gf.init(menagerie);
	nshark = nfish = 0;
	setsize(1);
	while (ncell < 1000) setsize(nsize + 1);
	setnfish(ncell - 1);
	setnshark(1);
	sl_sharkbreed.setvalues(shark.genbreed, 1, 100);
	sl_fishbreed.setvalues(fish.genbreed, 1, 100);
	sl_sharkstarve.setvalues(shark.genstarve, 1, 100);
	sl_delay.setvalues(10, 0, 100);
	remixflag = true;
	mutex = new Object();
	this.addComponentListener(new ComponentAdapter() {
		public void componentResized(ComponentEvent e) {
			pn_mp.resize();
			pn_gf.resize();
			remixflag = setsize();
		}
	});
	goflag = true;
}

private boolean setsize()
{
	pn_mp.getsize();
	return (setsize((int) ((long) nsize * Math.min(pn_mp.width, pn_mp.height) 
			/ nsizemax)));
}

private boolean setsize(int ansize)
{
	pn_mp.getsize();
	nsizemax = Math.min(pn_mp.width, pn_mp.height);
	nsize = Math.max(1, Math.min(nsizemax, ansize));
	nrow = nsize * pn_mp.height / nsizemax;
	ncol = nsize * pn_mp.width / nsizemax;
	ncell = nrow * ncol;
	sl_ncell.scrollbar.set(nsize, 1, nsizemax);
	sl_ncell.label.set(ncell, 1, pn_mp.width * pn_mp.height);
	if (nfish + nshark > ncell) {
		nfish = (int) ((long) ncell * nfish / (nfish + nshark));
		nshark = ncell - nfish;
	}
	sl_nshark.setvalues(nshark, 0, ncell);
	sl_nfish.setvalues(nfish, 0, ncell);
	return (true);
}

private boolean setnshark(int n)
{
	nshark = Math.min(ncell, n);
	sl_nshark.setvalue(nshark);
	if (nfish + nshark > ncell)
		sl_nfish.setvalue(nfish = ncell - nshark);
	return (true);
}

private boolean setnfish(int n)
{
	nfish = Math.min(ncell, n);
	sl_nfish.setvalue(nfish);
	if (nshark + nfish > ncell)
		sl_nshark.setvalue(nshark = ncell - nfish);
	return (true);
}

private boolean setsbreed(int n)
{
	shark.genbreed = n;
	sl_sharkbreed.setvalue(n);
	return (true);
}

private boolean setfbreed(int n)
{
	fish.genbreed = n;
	sl_fishbreed.setvalue(n);
	return (true);
}

private boolean setstarve(int n)
{
	shark.genstarve = n;
	sl_sharkstarve.setvalue(n);
	return (true);
}

private boolean setdelay(int n)
{
	delay = n;
	sl_delay.setvalue(n);
	return (false);
}

} // end class wator

class slider {

private static final long serialVersionUID = 42L;

public label label;
public scrollbar scrollbar;

public slider(String aname, Color acolor, 
		Container container, ActionListener aactionlistener)
{
	label = new label(aname, acolor, container);
	scrollbar = new scrollbar(container, aactionlistener, this);
}

void setvalue(int avalue)
{
	scrollbar.set(avalue);
	label.set(avalue);
}

void setvalues(int avalue, int amin, int amax)
{
	scrollbar.set(avalue, amin, amax);
	label.set(avalue, amin, amax);
}

} // end class slider

class label extends range {
	
Label label;
String name;

public label(String aname, Color acolor, Container container)
{
	name = aname;
	label = new Label();
	container.add(label);
	label.setForeground(acolor);
}

boolean set(int avalue)
{
	return (set(avalue, min, max));
}

boolean set(int avalue, int amin, int amax)
{
	if (!super.set(avalue, amin, amax)) return (false);
	label.setText(name + " " + Integer.toString(value)
			+ " [" + Integer.toString(min)
			+ "-" + Integer.toString(max) + "]");
	return (true);
}

} // end class label

class scrollbar extends range implements AdjustmentListener {
	
Scrollbar scrollbar;
ActionListener actionlistener;
Object source;

public scrollbar(Container container, 
		ActionListener aactionlistener, Object asource)
{
	scrollbar = new Scrollbar(0);
	container.add(scrollbar);
	scrollbar.addAdjustmentListener(this);
	actionlistener = aactionlistener;
	source = asource;
}

boolean set(int avalue)
{
	return (set(avalue, min, max));
}

boolean set(int avalue, int amin, int amax)
{
	int width;
	
	if (!super.set(avalue, amin, amax)) return (false);
	max = Math.max(min + 1, max);
	width = Math.max(1, (max - min) / 10);
	scrollbar.setValues(value, width, min, max + width);
	return (true);
}

public void adjustmentValueChanged(AdjustmentEvent e)
{
	value = scrollbar.getValue();
	actionlistener.actionPerformed(new ActionEvent(source, 
			ActionEvent.ACTION_PERFORMED, null));
}

} // end class scrollbar

class range {

int value, min, max;

range()
{
	value = 0;
	min = 0;
	max = 1;
}

boolean set(int avalue)
{
	avalue = Math.max(min, Math.min(max, avalue));
	if (value == avalue) return (false);
	value = avalue;
	return (true);
}

boolean set(int avalue, int amin, int amax)
{
	avalue = Math.max(amin, Math.min(amax, avalue));
	if (value == avalue && min == amin && max == amax) return (false);
	value = avalue;
	min = amin;
	max = amax;
	return (true);
}

} // end class range

class button extends Button {
	
private static final long serialVersionUID = 42L;

public button(String name, Container container,
		ActionListener aactionlistener)
{
	super(name);
	container.add(this);
	addActionListener(aactionlistener);
}

} // end class button

class panelmap extends Panel {

private static final long serialVersionUID = 42L;

menagerie menagerie;
public rnd rnd;
int nrow, ncol;
int height, width;
cell celltab[][];
drawdim drawrow, drawcol;
individual individualhed;
boolean paintsyncflag;
Applet applet;

public panelmap(menagerie amenagerie)
{
	menagerie = amenagerie;
	rnd = new rnd();
	menagerie.rnd = rnd;
	setBackground(menagerie.ocean.color);
	getsize();
	individualhed = new individual(null, null);
	redim(1, 1);
}

void getsize()
{
	height = Math.max(1, getSize().height - 2);
	width = Math.max(1, getSize().width - 2);
}

public void remix(int anrow, int ancol)
{
	getsize();
	redim(anrow, ancol);
	remake(menagerie.fish.cnt, menagerie.shark.cnt);
}

public void redim(int anrow, int ancol)
{
	int irow, icol;

	individualhed.unlink();
	menagerie.clearcnts();
	drawrow = new drawdim(nrow = anrow);
	drawcol = new drawdim(ncol = ancol);
	celltab = new cell[nrow][ncol];
	drawset();
	for (irow = 0; irow < nrow; irow++)
		for (icol = 0; icol < ncol; icol++)
			celltab[irow][icol] = new cell(menagerie.ocean,
				drawrow.getseg(irow), drawcol.getseg(icol));
	for (irow = 0; irow < nrow; irow++)
		for (icol = 0; icol < ncol; icol++)
			celltab[irow][icol].setneighbors(celltab, 
					irow, icol, nrow, ncol);
}

public void resize()
{
	getsize();
	drawset();
}

public void drawset()
{
	drawrow.set(height);
	drawcol.set(width);
}

public void remake(int anfish, int anshark)
{
	cell celllist[];
	species specieslist[];
	int irow, icol, ncell, icell, nindividual, iindividual;

	while (individualhed.next != individualhed)
		individualhed.next.die();
	celllist = new cell[nrow * ncol];
	ncell = 0;
	for (irow = 0; irow < nrow; irow++)
		for (icol = 0; icol < ncol; icol++)
			celllist[ncell++] = celltab[irow][icol];
	specieslist = new species[anfish + anshark];
	nindividual = 0;
	while (anfish-- > 0) 
		specieslist[nindividual++] = menagerie.fish;
	while (anshark-- > 0) 
		specieslist[nindividual++] = menagerie.shark;
	while (ncell > 0 && nindividual > 0) {
		icell = rnd.get(ncell);
		iindividual = rnd.get(nindividual);
		celllist[icell].individual.breed
			(specieslist[iindividual], individualhed);
		celllist[icell] = celllist[--ncell];
		specieslist[iindividual] = specieslist[--nindividual];
	}
}

public void gen()
{
	individual individual;

	menagerie.graphics = paintsyncflag? null: getGraphics();
	for (individual = individualhed.next; individual != individualhed;
			individual = individual.gen());
}

public void paint(Graphics g)
{
	paintsyncflag = true;
	applet.start();
}

public void paintupdate()
{
	Image i;
	individual individual;
	Graphics graphics;

	if (!paintsyncflag) return;
	paintsyncflag = false;
	i = createImage(getSize().width, getSize().height);
	graphics = i.getGraphics();
	menagerie.graphics = graphics;
	graphics.setColor(menagerie.ocean.color);
	graphics.fillRect(0, 0, width + 2, height + 2);
	graphics.setColor(menagerie.foregroundcolor);
	graphics.drawRect(0, 0, width + 1, height + 1);
	for (individual = individualhed.next; individual != individualhed;
			individual = individual.next)
		individual.draw();
	getGraphics().drawImage(i, 0, 0, this);
}

} // end class panelmap

class drawdim {

drawseg tab[];
int n;

public drawdim(int an)
{
	int i;

	n = an;
	tab = new drawseg[an];
	for (i = 0; i < n; i++) tab[i] = new drawseg();
}

public void set(int awidth)
{
	int nextoffset, i;

	nextoffset = 0;
	for (i = 0; i < n; i++)
		nextoffset = tab[i].set(nextoffset, awidth * (i + 1) / n);
}

public drawseg getseg(int i)
{
	return (tab[i]);
}

} // end class drawdim

class drawseg {

public int offset;
public int width;

public int set(int aoffset, int anextoffset)
{
	offset = aoffset;
	width = anextoffset - aoffset;
	return (anextoffset);
}

} // end class drawseg

class menagerie {

public species ocean, fish, shark;
public Color backgroundcolor, foregroundcolor;
public rnd rnd;
public Graphics graphics;

public menagerie()
{
	foregroundcolor = new Color(0, 0, 0);
	backgroundcolor = new Color(255, 255, 255);
	ocean = new species(this, new Color(128, 128, 255), -1, -1, null);
	fish = new species(this, new Color(0, 64, 0), 3, -1, null);
	shark = new species(this, new Color(128, 0, 0), 10, 3, fish);
}

public void clearneighborcnts()
{
	ocean.neighborcnt = 0;
	fish.neighborcnt = 0;
	shark.neighborcnt = 0;
}

public void clearcnts()
{
	ocean.cnt = 0;
	fish.cnt = 0;
	shark.cnt = 0;
}

public void drawcell(Color color, drawseg row, drawseg col)
{
	if (graphics == null) return;
	graphics.setColor(color);
	graphics.fillRect(col.offset + 1, row.offset + 1,
		col.width, row.width);
}

} // end class menagerie

class species {

public int cnt;
menagerie menagerie;
public int genbreed;
public int genstarve;
public species food;
individual neighbortab[];
int neighborcnt;
Color color;

public species(menagerie amenagerie, Color acolor,
		int agenbreed, int agenstarve, species afood)
{
	menagerie = amenagerie;
	genbreed = agenbreed;
	genstarve = agenstarve;
	food = afood;
	color = acolor;
	neighbortab = new individual[4];
}

public void tallyneighbor(individual aneighbor)
{
	neighbortab[neighborcnt++] = aneighbor;
}

public individual pickneighbor()
{
	return (neighborcnt != 0? 
			neighbortab[menagerie.rnd.get(neighborcnt)]: null);
}

public individual pickfood()
{
	return (food != null? food.pickneighbor(): null);
}

public individual pickocean()
{
	return (menagerie.ocean.pickneighbor());
}

public void inccnt(int delta)
{
	menagerie.ocean.cnt -= delta;
	cnt += delta;
}

} // end class species
			
class cell {

public cell neighbortab[];
public individual individual;
public drawseg drawrow, drawcol;

public cell(species aspecies, drawseg adrawrow, drawseg adrawcol)
{
	neighbortab = new cell[4];
	individual = new individual(this, aspecies);
	drawrow = adrawrow;
	drawcol = adrawcol;
}

public void setneighbors(cell celltab[][],
		int irow, int icol, int nrow, int ncol)
{
	int dir, drow, dcol, tmp;
	
	for (dir = 0, drow = 1, dcol = 0; dir < 4; 
			dir++, tmp = drow, drow = dcol, dcol = -tmp)
		neighbortab[dir] = celltab[(irow + drow + nrow) % nrow]
		                           [(icol + dcol + ncol) % ncol];
}

public void swapindividual(cell acell)
{
	individual e;
	
	e = individual;
	individual = acell.individual;
	acell.individual = e;
}

public void swap(cell acell)
{
	individual.swapcell(acell.individual);
	swapindividual(acell);
}

public void dealneighbors()
{
	int dir;

	for (dir = 0; dir < 4; dir++)
		neighbortab[dir].individual.tallyneighbor();
}

} // end class cell

class individual {
	
cell cell;
individual prev, next;
public species species;
int genbreed;
int genstarve;

public individual(cell acell, species aspecies)
{
	cell = acell;
	prev = next = this;
	species = aspecies;
	if (species != null) species.cnt++;
}

public void tallyneighbor()
{
	species.tallyneighbor(this);
}

public individual gen()
{
	individual food, open, oldnext;

	if (genbreed > 0) genbreed--;
	if (genstarve > 0) genstarve--;
	species.menagerie.clearneighborcnts();
	cell.dealneighbors();
	open = food = species.pickfood();
	if (food != null) {
		genstarve = species.genstarve;
		food.die();
	}
	else if (genstarve == 0) open = this;
	else open = species.pickocean();
	if (open != null) swap(open);
	oldnext = next;
	if (open != null && genbreed == 0) {
		genbreed = species.genbreed;
		open.breed(species, oldnext);
	}
	else if (genstarve == 0) die();
	if (open != null) {
		draw();
		if (open != this) open.draw();
	}
	return (oldnext);
}

public void draw()
{
	species.menagerie.drawcell(species.color, 
			cell.drawrow, cell.drawcol);
}

public void die()
{
	unlink();
	setspecies(species.menagerie.ocean);
}

public void breed(species aspecies, individual anext)
{
	unlink();
	setspecies(aspecies);
	append(anext);
	genbreed = species.menagerie.rnd.get2(species.genbreed);
	genstarve = species.menagerie.rnd.get2(species.genstarve);
}

public void setspecies(species aspecies)
{
	species.cnt--;
	species = aspecies;
	species.cnt++;
}

public void swapcell(individual aindividual)
{
	cell c;
	
	c = cell;
	cell = aindividual.cell;
	aindividual.cell = c;
}

public void swap(individual aindividual)
{
	cell.swapindividual(aindividual.cell);
	swapcell(aindividual);
}

public void append(individual aindividual)
{
	next.prev = aindividual.prev;
	aindividual.prev.next = next;
	next = aindividual;
	aindividual.prev = this;
}

public void insert(individual aindividual)
{
	aindividual.append(this);
}

public void unlink()
{
	next.prev = prev;
	prev.next = next;
	next = prev = this;
}

} // end class individual

class rnd {
	
Random random;
		
public rnd()
{
	random = new Random();
}

public int get(int n)
{
	return (n <= 1? n == 1? 0: n:
		random.nextInt(n));
}

public int get2(int n)
{
	return (get(n) + get(n) + 1);
}

} // end class rnd

class panelgrf extends Panel {

private static final long serialVersionUID = 42L;

menagerie menagerie;
int width;
int height;
int pos;
boolean cont;
graphgen gentab[];
Graphics graphics;
Object mutex;

public panelgrf()
{
	mutex = new Object();
}

public void init(menagerie amenagerie)
{
	menagerie = amenagerie;
	setBackground(menagerie.ocean.color);
	resize();
}

public void resize()
{
	resize(getSize().height - 2, getSize().width - 2);
}

public void resize(int aheight, int awidth)
{
	int i;
	
	height = aheight;
	width = awidth;
	gentab = new graphgen[awidth];
	for (i = 0; i < awidth; i++)
		gentab[i] = new graphgen(menagerie.ocean.color);
	pos = 0;
}

public void brk()
{
	if (!cont) return;
	cont = false;
	gentab[pos].set(menagerie.backgroundcolor);
	sweep();
}

public void gen(int ncell, int nfish, int nshark)
{
	cont = true;
	gentab[pos].set(menagerie.ocean.color, ncell, nfish, nshark);
	sweep();
}

public void sweep()
{
	int newpos;
	
	newpos = (pos + 1) % width;
	gentab[newpos].set(menagerie.foregroundcolor);
	synchronized (mutex) {
		graphics = getGraphics();
		drawgen(newpos);
		drawgen(pos);
	}
	pos = newpos;
}

public void paint(Graphics g)
{
	int i;

	synchronized (mutex) {
		graphics = g;
		graphics.setColor(menagerie.foregroundcolor);
		graphics.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
		for (i = 0; i < width; i++) drawgen(i);
	}
}

public void drawgen(int pos)
{
	graphgen curr, prev;
	
	if (graphics == null) return;
	curr = gentab[pos];
	prev = gentab[(pos + width - 1) % width];
	graphics.setColor(curr.color);
	graphics.drawLine(pos + 1, 1, pos + 1, height);
	graphics.setColor(menagerie.fish.color);
	draw(pos, curr.nfish, curr.ncell, prev.nfish, prev.ncell);
	graphics.setColor(menagerie.shark.color);
	draw(pos, curr.nshark, curr.ncell, prev.nshark, prev.ncell);
}

public void draw(int pos, int numcurr, int dencurr, int numprev, int denprev)
{
	int prev, curr;
	
	if (numcurr == -1) return;
	if (numprev == -1) { numprev = numcurr; denprev = dencurr; }
	curr = (int) ((long) (height - 1) * (dencurr - numcurr) / dencurr);
	prev = (int) ((long) (height - 1) * (denprev - numprev) / denprev);
	if (prev < curr) prev++;
	if (prev > curr) prev--;
	graphics.drawLine(pos + 1, prev + 1, pos + 1, curr + 1);
}

} // end class panelgrf

class graphgen {

Color color;
int ncell;
int nfish;
int nshark;
	
public graphgen(Color acolor)
{
	set(acolor);
}

public void set(Color acolor, int ancell, int anfish, int anshark)
{
	color = acolor;
	ncell = ancell;
	nfish = anfish;
	nshark = anshark;
}

public void set(Color acolor)
{
	set(acolor, -1, -1, -1);
}

} // end class graphgen
