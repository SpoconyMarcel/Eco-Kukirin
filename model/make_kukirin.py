"""Generuje prosty model 3D hulajnogi Kukirin (OBJ + MTL). Os Z = gora, +Y = przod, jednostki = metry."""
import math

verts, faces, mtls = [], [], {}
cur = None
def use(m): 
    global cur; cur = m
    faces.append(("usemtl", m))
def add(vs, fs):
    o = len(verts)
    verts.extend(vs)
    for f in fs: faces.append(("f", [i+o+1 for i in f]))

def box(cx,cy,cz,sx,sy,sz):
    x,y,z=sx/2,sy/2,sz/2
    vs=[(cx+a*x,cy+b*y,cz+c*z) for a in(-1,1) for b in(-1,1) for c in(-1,1)]
    fs=[(0,1,3,2),(4,6,7,5),(0,4,5,1),(2,3,7,6),(0,2,6,4),(1,5,7,3)]
    add(vs,fs)

def cyl(p0,p1,r,seg=24,r1=None):
    """cylinder miedzy punktami p0,p1"""
    r1 = r if r1 is None else r1
    d=[p1[i]-p0[i] for i in range(3)]; L=math.sqrt(sum(c*c for c in d)); d=[c/L for c in d]
    a=(1,0,0) if abs(d[0])<0.9 else (0,1,0)
    u=[d[1]*a[2]-d[2]*a[1], d[2]*a[0]-d[0]*a[2], d[0]*a[1]-d[1]*a[0]]
    n=math.sqrt(sum(c*c for c in u)); u=[c/n for c in u]
    v=[d[1]*u[2]-d[2]*u[1], d[2]*u[0]-d[0]*u[2], d[0]*u[1]-d[1]*u[0]]
    vs=[]
    for p,rr in((p0,r),(p1,r1)):
        for k in range(seg):
            t=2*math.pi*k/seg
            vs.append(tuple(p[i]+rr*(math.cos(t)*u[i]+math.sin(t)*v[i]) for i in range(3)))
    fs=[(k,(k+1)%seg,seg+(k+1)%seg,seg+k) for k in range(seg)]
    fs.append(tuple(range(seg-1,-1,-1))); fs.append(tuple(range(seg,2*seg)))
    add(vs,fs)

def wheel(y,z=0.13):
    R=0.13
    use("tire");  cyl((-0.045,y,z),(0.045,y,z),R,36)
    use("rim");   cyl((-0.047,y,z),(0.047,y,z),R*0.62,36)
    use("hub");   cyl((-0.05,y,z),(0.05,y,z),0.025,16)

WB=0.62   # polowa rozstawu osi
wheel(+WB); wheel(-WB)
# deska
use("deck");  box(0,0,0.20,0.17,0.78,0.045)
use("grip");  box(0,0.02,0.2265,0.15,0.70,0.008)
# bateria pod deska
use("battery"); box(0,0.0,0.155,0.13,0.50,0.04)
# blotniki
use("body"); box(0,-WB,0.285,0.11,0.30,0.012); box(0,WB,0.285,0.11,0.26,0.012)
# widelec przedni + kolumna
use("body")
cyl((-0.06,WB,0.13),(-0.03,0.50,0.95),0.014)
cyl((0.06,WB,0.13),(0.03,0.50,0.95),0.014)
use("stem");  cyl((0,0.48,0.20),(0,0.50,1.08),0.022)
# zlaczka skladania
use("accent"); cyl((-0.035,0.45,0.30),(0.035,0.45,0.30),0.03,16)
# kierownica
use("stem");  cyl((-0.30,0.50,1.08),(0.30,0.50,1.08),0.012,16)
use("grip");  cyl((-0.30,0.50,1.08),(-0.20,0.50,1.08),0.016,16); cyl((0.20,0.50,1.08),(0.30,0.50,1.08),0.016,16)
# wyswietlacz i latarka
use("screen"); box(0,0.49,1.12,0.07,0.025,0.04)
use("light");  box(0,0.53,0.80,0.05,0.02,0.035)
# tyl: swiatlo stop + stopka
use("red");    box(0,-0.50,0.23,0.06,0.02,0.02)
use("body");   cyl((0.07,-0.30,0.18),(0.10,-0.36,0.07),0.008,10)

colors={"tire":(0.05,0.05,0.05),"rim":(0.75,0.75,0.78),"hub":(0.3,0.3,0.3),"deck":(0.12,0.12,0.14),
"grip":(0.02,0.02,0.02),"battery":(0.2,0.2,0.22),"body":(0.15,0.55,0.25),"stem":(0.1,0.1,0.12),
"accent":(0.9,0.5,0.1),"screen":(0.0,0.1,0.15),"light":(1.0,0.97,0.8),"red":(0.9,0.05,0.05)}
with open("EcoKukirin.mtl","w") as f:
    for n,c in colors.items(): f.write(f"newmtl {n}\nKd {c[0]} {c[1]} {c[2]}\nKa 0.1 0.1 0.1\n\n")
with open("EcoKukirin.obj","w") as f:
    f.write("mtllib EcoKukirin.mtl\no EcoKukirin\n")
    for v in verts: f.write("v %.4f %.4f %.4f\n"%v)
    for k,a in faces:
        f.write(f"usemtl {a}\n" if k=="usemtl" else "f "+" ".join(map(str,a))+"\n")
print(len(verts),"vertices")
