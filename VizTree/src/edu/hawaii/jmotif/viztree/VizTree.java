package edu.hawaii.jmotif.viztree;

public class VizTree {
      private int SCROLL_INCREMENT = 100;
      private const int MAX_SURPRISE = 10;
      
      private double default_scale;

      private PrintDocument printDoc = new PrintDocument();
      private Options Op = new Options(3, true);
      private ArrayList alData = new ArrayList();
      private ArrayList alData2 = new ArrayList();
      private ArrayList surprising = new ArrayList();
      private ArrayList scores = new ArrayList();
      private ArrayList curOffset = new ArrayList();    // for the high-lights
      private int curWeight;        // also fot the high-lights
      private Tree t;
//      private Tree t2;
      private SuffixTree s;
      private SuffixTree s2;
      private int total_weight;
      private int total_weight2;
      private int window_len;
      private int num_seg;
      private byte alphabet_size;
      private byte NR_option;
      private bool NORM;
      private int root_pos;
      private double plot_scale;
      private int start_pos;
      private int end_pos;
      private int zoom_level;
      private int scroll;     // increment 1 if scroll to the right; decrement 1 if scroll to the left
      private int max_len;    // the larger of the two time series (if two are loaded)
      private int w = System.Windows.Forms.SystemInformation.WorkingArea.Width;
      private int h = System.Windows.Forms.SystemInformation.WorkingArea.Height;
      private byte[] pathZoomedIn;
      private bool added_second;
      //private double min_norm;
      //private double max_norm;
      private System.Windows.Forms.MainMenu mainMenu1;
      private System.Windows.Forms.MenuItem menuItem1;
      private System.Windows.Forms.MenuItem menuOpen;
      private System.Windows.Forms.OpenFileDialog dlgOpenFile;
      private System.Windows.Forms.Label labelFilename;
      private System.Windows.Forms.PictureBox pbTree;
      private System.Windows.Forms.PictureBox pbTS;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label2;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.GroupBox groupBoxParam;
      private System.Windows.Forms.TextBox txtWindowLen;
      private System.Windows.Forms.TextBox txtNumSeg;
      private System.Windows.Forms.ComboBox comboAlphabet;
      private System.Windows.Forms.Button buttonShowTree;
      private System.Windows.Forms.PictureBox pbZoomIn;
      private System.Windows.Forms.PictureBox pbSubseq;
      private System.Windows.Forms.Button buttonOption;
      private System.Windows.Forms.MenuItem menuAdd;
      private System.Windows.Forms.Button buttonAddData;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.RadioButton radioViewFirst;
      private System.Windows.Forms.RadioButton radioViewSecond;
      private System.Windows.Forms.RadioButton radioViewDiff;
      private System.Windows.Forms.MenuItem menuPrint;
      private System.Windows.Forms.Label labelFilename2;
      private System.Windows.Forms.Button buttonLeft;
      private System.Windows.Forms.Button buttonRight;
      private System.Windows.Forms.Button buttonZoomOut;
      private System.Windows.Forms.Button buttonZoomIn;
      private System.Windows.Forms.Button buttonReset;
//      private System.Windows.Forms.ComboBox cmboSurprise;

      /// <summary>
      /// Required designer variable.
      /// </summary>
      private System.ComponentModel.Container components = null;
      
      public VizTree()
      {
        //
        // Required for Windows Form Designer support
        //
        InitializeComponent();

        //
        // TODO: Add any constructor code after InitializeComponent call
        //
        this.ClientSize = new System.Drawing.Size(w,h);
        this.labelFilename.Location = new System.Drawing.Point((int)(w*0.015),
                                     (int)(h*0.01));

        this.labelFilename2.Location = new System.Drawing.Point((int)(w*0.015),(int)(h*0.03));

        labelFilename.ForeColor = Color.Blue;
        labelFilename2.ForeColor = Color.DarkGreen;

        // y-coordinate of the filename label
        int filename_bottom = (int)(h*0.03) + this.labelFilename2.Size.Height;

        int pbTS_top = filename_bottom;// + (int)(h*0.001);

        this.pbTS.Location = new System.Drawing.Point((int)(w*0.015), pbTS_top);
        this.pbTS.Size = new System.Drawing.Size((int)(w*0.6), 196);
        int pbTS_bottom = pbTS_top + 196;

        this.groupBoxParam.Location = new System.Drawing.Point((int)(w*0.63),pbTS_top);


//        this.cmboSurprise.Location = new System.Drawing.Point((int)this.groupBoxParam.Left, (int)this.labelFilename2.Top);

//        int gbParam_bottom = 

    //    this.buttonShowTree.Location = new System.Drawing.Point((int)(w*0.65), gbParam_bottom + (int)(h * 0.02));

        int arrow_w = buttonLeft.Width;
        int arrow_h = buttonLeft.Height;
        

        this.pbTree.Location = new System.Drawing.Point((int)(w*0.015),pbTS_bottom + (int)(h*0.015));

        this.buttonLeft.Location = new System.Drawing.Point((int)(pbTS.Right - 2 * arrow_w), (int)(this.pbTS.Top - arrow_h - h * 0.005));
        this.buttonRight.Location = new System.Drawing.Point((int)(pbTS.Right - arrow_w), (int)(this.pbTS.Top - arrow_h - h * 0.005));
        this.buttonZoomOut.Location = new System.Drawing.Point((int)(buttonLeft.Left - buttonZoomOut.Width), (int)(this.pbTS.Top - arrow_h - h * 0.005));
        this.buttonZoomIn.Location = new System.Drawing.Point((int)(buttonZoomOut.Left - buttonZoomIn.Width), (int)(this.pbTS.Top - arrow_h - h * 0.005));

        this.buttonReset.Location = new System.Drawing.Point(this.groupBoxParam.Left, this.buttonLeft.Top);

        // Leave 10% space in the bottom and allocate all the space left to pbTree
        int pbTree_height = h - pbTS_bottom - (int)(h*0.1);
        this.pbTree.Size = new System.Drawing.Size((int)(w*0.6), pbTree_height);
        
        this.pbZoomIn.Location = new System.Drawing.Point((int)(w*0.63), pbTS_bottom + (int)(h*0.015));

        int pbZoomIn_height = (int)((h - pbTS_bottom - (int)(h*0.015)) * 0.5);

        this.pbZoomIn.Size = new System.Drawing.Size((int)(w*0.35), pbZoomIn_height);
        this.pbZoomIn.MouseUp += new System.Windows.Forms.MouseEventHandler(this.pbZoomIn_MouseUp);

        this.pbSubseq.Location = new System.Drawing.Point((int)(w*0.63),pbTS_bottom + (int)(h*0.03) + pbZoomIn_height);

        this.pbSubseq.Size = new System.Drawing.Size((int)(w*0.35), h - pbTS_bottom - pbZoomIn_height - (int)(h * 0.115));

        this.pbTree.MouseUp += new System.Windows.Forms.MouseEventHandler(this.pbTree_MouseUp);

      //  curOffset = new ArrayList();

        Initialize();
      }

    /*  public byte NR_OPT
      {
        get
        {
          return NR_option;
        }
        set
        {
          NR_option = value;
        }
      }*/

      public void Initialize()
      {
        NR_option = 3;

        NORM = true;
        pathZoomedIn = new byte[1];

        root_pos = 10;
        
        radioViewSecond.Enabled = false;
        radioViewDiff.Enabled = false;

        buttonLeft.Enabled = false;
        buttonRight.Enabled = false;
        buttonZoomIn.Enabled = false;
        buttonZoomOut.Enabled = false;
        buttonReset.Enabled = false;

        this.labelFilename.Text = "";
        this.labelFilename2.Text = "";
        this.groupBoxParam.Enabled = false;

        this.menuAdd.Enabled = false;

        scroll = 0;
        start_pos = 0;
        zoom_level = 0;
//        plot_scale = DEFAULT_SCALE;
        //      DEFAULT_SCALE = 0.3;

        curWeight = 0;

        this.comboAlphabet.SelectedIndex = 2;

        alData.Clear();
        alData2.Clear();
        curOffset.Clear();
        
        surprising.Clear();
        scores.Clear();
        
      
        
      }

      /// <summary>
      /// Clean up any resources being used.
      /// </summary>
      protected override void Dispose( bool disposing )
      {
        if( disposing )
        {
          if (components != null) 
          {
            components.Dispose();
          }
        }
        base.Dispose( disposing );
      }

      #region Windows Form Designer generated code
      /// <summary>
      /// Required method for Designer support - do not modify
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
        this.pbTree = new System.Windows.Forms.PictureBox();
        this.mainMenu1 = new System.Windows.Forms.MainMenu();
        this.menuItem1 = new System.Windows.Forms.MenuItem();
        this.menuOpen = new System.Windows.Forms.MenuItem();
        this.menuAdd = new System.Windows.Forms.MenuItem();
        this.menuPrint = new System.Windows.Forms.MenuItem();
        this.dlgOpenFile = new System.Windows.Forms.OpenFileDialog();
        this.pbTS = new System.Windows.Forms.PictureBox();
        this.labelFilename = new System.Windows.Forms.Label();
        this.groupBoxParam = new System.Windows.Forms.GroupBox();
        this.groupBox1 = new System.Windows.Forms.GroupBox();
        this.radioViewDiff = new System.Windows.Forms.RadioButton();
        this.radioViewSecond = new System.Windows.Forms.RadioButton();
        this.radioViewFirst = new System.Windows.Forms.RadioButton();
        this.buttonAddData = new System.Windows.Forms.Button();
        this.buttonOption = new System.Windows.Forms.Button();
        this.label3 = new System.Windows.Forms.Label();
        this.label2 = new System.Windows.Forms.Label();
        this.label1 = new System.Windows.Forms.Label();
        this.txtWindowLen = new System.Windows.Forms.TextBox();
        this.txtNumSeg = new System.Windows.Forms.TextBox();
        this.comboAlphabet = new System.Windows.Forms.ComboBox();
        this.buttonShowTree = new System.Windows.Forms.Button();
        this.pbZoomIn = new System.Windows.Forms.PictureBox();
        this.pbSubseq = new System.Windows.Forms.PictureBox();
        this.labelFilename2 = new System.Windows.Forms.Label();
        this.buttonLeft = new System.Windows.Forms.Button();
        this.buttonRight = new System.Windows.Forms.Button();
        this.buttonZoomOut = new System.Windows.Forms.Button();
        this.buttonZoomIn = new System.Windows.Forms.Button();
        this.buttonReset = new System.Windows.Forms.Button();
        this.groupBoxParam.SuspendLayout();
        this.groupBox1.SuspendLayout();
        this.SuspendLayout();
        // 
        // pbTree
        // 
        this.pbTree.BackColor = System.Drawing.Color.White;
        this.pbTree.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
        this.pbTree.Location = new System.Drawing.Point(16, 248);
        this.pbTree.Name = "pbTree";
        this.pbTree.Size = new System.Drawing.Size(536, 416);
        this.pbTree.TabIndex = 0;
        this.pbTree.TabStop = false;
        // 
        // mainMenu1
        // 
        this.mainMenu1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                              this.menuItem1});
        // 
        // menuItem1
        // 
        this.menuItem1.Index = 0;
        this.menuItem1.MenuItems.AddRange(new System.Windows.Forms.MenuItem[] {
                                              this.menuOpen,
                                              this.menuAdd,
                                              this.menuPrint});
        this.menuItem1.Text = "File";
        // 
        // menuOpen
        // 
        this.menuOpen.Index = 0;
        this.menuOpen.Text = "Open...";
        this.menuOpen.Click += new System.EventHandler(this.menuOpen_Click);
        // 
        // menuAdd
        // 
        this.menuAdd.Enabled = false;
        this.menuAdd.Index = 1;
        this.menuAdd.Text = "Add...";
        this.menuAdd.Click += new System.EventHandler(this.menuAdd_Click);
        // 
        // menuPrint
        // 
        this.menuPrint.Index = 2;
        this.menuPrint.Text = "Print";
        this.menuPrint.Click += new System.EventHandler(this.menuPrint_Click);
        // 
        // dlgOpenFile
        // 
        this.dlgOpenFile.Filter = "Text Documents (*.txt)|*.txt|Data Files (*.dat)|*.dat|All Files|*.*";
        this.dlgOpenFile.FilterIndex = 2;
        // 
        // pbTS
        // 
        this.pbTS.BackColor = System.Drawing.Color.White;
        this.pbTS.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
        this.pbTS.Location = new System.Drawing.Point(16, 40);
        this.pbTS.Name = "pbTS";
        this.pbTS.Size = new System.Drawing.Size(536, 192);
        this.pbTS.TabIndex = 1;
        this.pbTS.TabStop = false;
        // 
        // labelFilename
        // 
        this.labelFilename.Location = new System.Drawing.Point(24, 8);
        this.labelFilename.Name = "labelFilename";
        this.labelFilename.Size = new System.Drawing.Size(776, 24);
        this.labelFilename.TabIndex = 2;
        // 
        // groupBoxParam
        // 
        this.groupBoxParam.Controls.Add(this.groupBox1);
        this.groupBoxParam.Controls.Add(this.buttonAddData);
        this.groupBoxParam.Controls.Add(this.buttonOption);
        this.groupBoxParam.Controls.Add(this.label3);
        this.groupBoxParam.Controls.Add(this.label2);
        this.groupBoxParam.Controls.Add(this.label1);
        this.groupBoxParam.Controls.Add(this.txtWindowLen);
        this.groupBoxParam.Controls.Add(this.txtNumSeg);
        this.groupBoxParam.Controls.Add(this.comboAlphabet);
        this.groupBoxParam.Controls.Add(this.buttonShowTree);
        this.groupBoxParam.Enabled = false;
        this.groupBoxParam.Location = new System.Drawing.Point(568, 36);
        this.groupBoxParam.Name = "groupBoxParam";
        this.groupBoxParam.Size = new System.Drawing.Size(312, 196);
        this.groupBoxParam.TabIndex = 3;
        this.groupBoxParam.TabStop = false;
        this.groupBoxParam.Text = "SAX Parameters";
        // 
        // groupBox1
        // 
        this.groupBox1.Controls.Add(this.radioViewDiff);
        this.groupBox1.Controls.Add(this.radioViewSecond);
        this.groupBox1.Controls.Add(this.radioViewFirst);
        this.groupBox1.Location = new System.Drawing.Point(24, 96);
        this.groupBox1.Name = "groupBox1";
        this.groupBox1.Size = new System.Drawing.Size(176, 88);
        this.groupBox1.TabIndex = 8;
        this.groupBox1.TabStop = false;
        this.groupBox1.Text = "Tree View";
        // 
        // radioViewDiff
        // 
        this.radioViewDiff.Location = new System.Drawing.Point(24, 60);
        this.radioViewDiff.Name = "radioViewDiff";
        this.radioViewDiff.TabIndex = 2;
        this.radioViewDiff.Text = "Difference Tree";
        // 
        // radioViewSecond
        // 
        this.radioViewSecond.Location = new System.Drawing.Point(24, 38);
        this.radioViewSecond.Name = "radioViewSecond";
        this.radioViewSecond.Size = new System.Drawing.Size(128, 24);
        this.radioViewSecond.TabIndex = 1;
        this.radioViewSecond.Text = "Second Time Series";
        // 
        // radioViewFirst
        // 
        this.radioViewFirst.Checked = true;
        this.radioViewFirst.Location = new System.Drawing.Point(24, 16);
        this.radioViewFirst.Name = "radioViewFirst";
        this.radioViewFirst.Size = new System.Drawing.Size(144, 24);
        this.radioViewFirst.TabIndex = 0;
        this.radioViewFirst.TabStop = true;
        this.radioViewFirst.Text = "First Time Series";
        // 
        // buttonAddData
        // 
        this.buttonAddData.Location = new System.Drawing.Point(216, 108);
        this.buttonAddData.Name = "buttonAddData";
        this.buttonAddData.TabIndex = 7;
        this.buttonAddData.Text = "Add Data";
        this.buttonAddData.Click += new System.EventHandler(this.buttonAddData_Click);
        // 
        // buttonOption
        // 
        this.buttonOption.Location = new System.Drawing.Point(216, 132);
        this.buttonOption.Name = "buttonOption";
        this.buttonOption.TabIndex = 6;
        this.buttonOption.Text = "Advanced";
        this.buttonOption.Click += new System.EventHandler(this.buttonOption_Click);
        // 
        // label3
        // 
        this.label3.Location = new System.Drawing.Point(25, 72);
        this.label3.Name = "label3";
        this.label3.Size = new System.Drawing.Size(96, 16);
        this.label3.TabIndex = 2;
        this.label3.Text = "Alphabet Size";
        // 
        // label2
        // 
        this.label2.Location = new System.Drawing.Point(24, 48);
        this.label2.Name = "label2";
        this.label2.Size = new System.Drawing.Size(144, 24);
        this.label2.TabIndex = 1;
        this.label2.Text = "# of Symbols per Window";
        // 
        // label1
        // 
        this.label1.Location = new System.Drawing.Point(24, 24);
        this.label1.Name = "label1";
        this.label1.Size = new System.Drawing.Size(120, 16);
        this.label1.TabIndex = 0;
        this.label1.Text = "Sliding Window Length";
        // 
        // txtWindowLen
        // 
        this.txtWindowLen.Location = new System.Drawing.Point(224, 22);
        this.txtWindowLen.MaxLength = 3;
        this.txtWindowLen.Name = "txtWindowLen";
        this.txtWindowLen.Size = new System.Drawing.Size(64, 20);
        this.txtWindowLen.TabIndex = 0;
        this.txtWindowLen.Text = "32";
        this.txtWindowLen.KeyDown += new System.Windows.Forms.KeyEventHandler(this.txtWindowLen_KeyDown);
        this.txtWindowLen.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.txtWindowLen_KeyPress);
        // 
        // txtNumSeg
        // 
        this.txtNumSeg.Location = new System.Drawing.Point(224, 44);
        this.txtNumSeg.MaxLength = 2;
        this.txtNumSeg.Name = "txtNumSeg";
        this.txtNumSeg.Size = new System.Drawing.Size(64, 20);
        this.txtNumSeg.TabIndex = 1;
        this.txtNumSeg.Text = "4";
        this.txtNumSeg.KeyDown += new System.Windows.Forms.KeyEventHandler(this.txtNumSeg_KeyDown);
        this.txtNumSeg.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.txtNumSeg_KeyPress);
        // 
        // comboAlphabet
        // 
        this.comboAlphabet.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
        this.comboAlphabet.Items.AddRange(new object[] {
                                   "2",
                                   "3",
                                   "4",
                                   "5",
                                   "6",
                                   "7",
                                   "8",
                                   "9",
                                   "10"});
        this.comboAlphabet.Location = new System.Drawing.Point(224, 67);
        this.comboAlphabet.Name = "comboAlphabet";
        this.comboAlphabet.Size = new System.Drawing.Size(64, 21);
        this.comboAlphabet.TabIndex = 2;
        // 
        // buttonShowTree
        // 
        this.buttonShowTree.Enabled = false;
        this.buttonShowTree.Location = new System.Drawing.Point(216, 156);
        this.buttonShowTree.Name = "buttonShowTree";
        this.buttonShowTree.TabIndex = 4;
        this.buttonShowTree.Text = "Show Tree";
        this.buttonShowTree.Click += new System.EventHandler(this.buttonShowTree_Click);
        // 
        // pbZoomIn
        // 
        this.pbZoomIn.BackColor = System.Drawing.Color.White;
        this.pbZoomIn.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
        this.pbZoomIn.Location = new System.Drawing.Point(568, 248);
        this.pbZoomIn.Name = "pbZoomIn";
        this.pbZoomIn.Size = new System.Drawing.Size(280, 176);
        this.pbZoomIn.TabIndex = 5;
        this.pbZoomIn.TabStop = false;
        // 
        // pbSubseq
        // 
        this.pbSubseq.BackColor = System.Drawing.Color.White;
        this.pbSubseq.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
        this.pbSubseq.Location = new System.Drawing.Point(568, 440);
        this.pbSubseq.Name = "pbSubseq";
        this.pbSubseq.Size = new System.Drawing.Size(280, 224);
        this.pbSubseq.TabIndex = 6;
        this.pbSubseq.TabStop = false;
        // 
        // labelFilename2
        // 
        this.labelFilename2.Location = new System.Drawing.Point(16, 16);
        this.labelFilename2.Name = "labelFilename2";
        this.labelFilename2.Size = new System.Drawing.Size(776, 23);
        this.labelFilename2.TabIndex = 7;
        // 
        // buttonLeft
        // 
        this.buttonLeft.Location = new System.Drawing.Point(472, 8);
        this.buttonLeft.Name = "buttonLeft";
        this.buttonLeft.Size = new System.Drawing.Size(40, 32);
        this.buttonLeft.TabIndex = 8;
        this.buttonLeft.Text = "<--";
        this.buttonLeft.Click += new System.EventHandler(this.buttonLeft_Click);
        // 
        // buttonRight
        // 
        this.buttonRight.Location = new System.Drawing.Point(512, 8);
        this.buttonRight.Name = "buttonRight";
        this.buttonRight.Size = new System.Drawing.Size(40, 32);
        this.buttonRight.TabIndex = 9;
        this.buttonRight.Text = "-->";
        this.buttonRight.Click += new System.EventHandler(this.buttonRight_Click);
        // 
        // buttonZoomOut
        // 
        this.buttonZoomOut.Location = new System.Drawing.Point(424, 8);
        this.buttonZoomOut.Name = "buttonZoomOut";
        this.buttonZoomOut.Size = new System.Drawing.Size(40, 32);
        this.buttonZoomOut.TabIndex = 10;
        this.buttonZoomOut.Text = "Zoom Out";
        this.buttonZoomOut.Click += new System.EventHandler(this.buttonZoomOut_Click);
        // 
        // buttonZoomIn
        // 
        this.buttonZoomIn.Location = new System.Drawing.Point(384, 8);
        this.buttonZoomIn.Name = "buttonZoomIn";
        this.buttonZoomIn.Size = new System.Drawing.Size(40, 32);
        this.buttonZoomIn.TabIndex = 11;
        this.buttonZoomIn.Text = "Zoom   In";
        this.buttonZoomIn.Click += new System.EventHandler(this.buttonZoomIn_Click);
        // 
        // buttonReset
        // 
        this.buttonReset.Location = new System.Drawing.Point(568, 8);
        this.buttonReset.Name = "buttonReset";
        this.buttonReset.TabIndex = 12;
        this.buttonReset.Text = "Reset";
        this.buttonReset.Click += new System.EventHandler(this.buttonReset_Click);
        // 
        // VizTree
        // 
        this.AutoScaleBaseSize = new System.Drawing.Size(5, 13);
        this.ClientSize = new System.Drawing.Size(968, 676);
        this.Controls.Add(this.buttonReset);
        this.Controls.Add(this.buttonZoomIn);
        this.Controls.Add(this.buttonZoomOut);
        this.Controls.Add(this.buttonRight);
        this.Controls.Add(this.buttonLeft);
        this.Controls.Add(this.labelFilename2);
        this.Controls.Add(this.pbSubseq);
        this.Controls.Add(this.pbZoomIn);
        this.Controls.Add(this.labelFilename);
        this.Controls.Add(this.pbTS);
        this.Controls.Add(this.pbTree);
        this.Controls.Add(this.groupBoxParam);
        this.Menu = this.mainMenu1;
        this.Name = "VizTree";
        this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
        this.Text = "VizTree";
        this.groupBoxParam.ResumeLayout(false);
        this.groupBox1.ResumeLayout(false);
        this.ResumeLayout(false);

      }
      #endregion


      /// <summary>
      /// The main entry point for the application.
      /// </summary>
      [STAThread]
      static void Main() 
      {
        Application.Run(new VizTree());
      }

  /*    private void BuildTree(ArrayList alSymbol, ArrayList alOffset, byte alphabet_size)
      {
        t = new Tree(alphabet_size, num_seg);
        
        byte[] temp;

        // Insert one string at a time
        for (int i = 0; i < alSymbol.Count; i++)
        {
          temp = (byte[])alSymbol[i];
          t.Insert(temp);
        }

        t.PrintBreadthFirst();
        //      t.PrintDepthFirst();
      }*/
          
      private void menuOpen_Click(object sender, System.EventArgs e)
      {
        if (dlgOpenFile.ShowDialog() == DialogResult.OK)
        {
          String filename = dlgOpenFile.FileName;
        
          s = new SuffixTree();

    //      alData = new ArrayList();
          alData = s.LoadFile(filename);    

          max_len = (alData.Count > alData2.Count ? alData.Count : alData2.Count);

          buttonLeft.Enabled = false;

          SCROLL_INCREMENT = 100;

          scroll = 0;

          plot_scale = (this.pbTS.Width * 0.98) / max_len;

    //      DEFAULT_SCALE = 0.3;

          //        ArrayList alSymbol = x.BuildMatrix(alData, 4, 4);

          //  BuildTree(alSymbol, 4);
          
          labelFilename.Text = filename;

          ArrayList dummy = new ArrayList();

          this.buttonRight.Enabled = true;
          this.buttonZoomIn.Enabled = true;
          this.buttonZoomOut.Enabled = true;
          this.buttonReset.Enabled = true;

          this.groupBoxParam.Enabled = true;
          this.buttonShowTree.Enabled = true;

          this.menuAdd.Enabled = true;

          t = null;

          Refresh(pbTree);
          Refresh(pbZoomIn);
          Refresh(pbSubseq);

          curOffset.Clear();
          surprising.Clear();
          scores.Clear();

          Plot(dummy, 0);

        }
      }

      // For subsequence tree, weight = highlight.Count. For diff-tree, weight can be used to determine
      // when the offsets for the first time series end.
      private void Plot(ArrayList highlight, int weight)
      {
        pbTS.Image = new Bitmap(pbTS.Width, pbTS.Height);

        // make sure the size is not 0
        if (pbTS.Width <= 0 | pbTS.Height <= 0)
          return;

        // get image properties
        Image im = pbTS.Image;
        double width = (pbTS.Width);
        double height = (pbTS.Height * 0.9);
        Graphics g = Graphics.FromImage( pbTS.Image );

        // 1: plot only the first
        // 2: plot only the second
        // 3: plot both
        int PLOT;

        g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

        Pen pen1 = new Pen(Color.Blue, 1);

        int min_len;

        if (alData2.Count == 0)
          min_len = alData.Count;
        else
          min_len = (alData.Count < alData2.Count ? alData.Count : alData2.Count);


      //  int start_pos = scroll * SCROLL_INCREMENT;
      //  int end_pos;

//        if (start_pos >= max_len)
//        if (start_pos >= data.Count)
        {
//          if ((index == 1 && data.Count < alData2.Count) || (index == 2 && data.Count < alData.Count))
//            return;
//          else
//          {
//            SCROLL_INCREMENT /= 2;
//            scroll = data.Count / SCROLL_INCREMENT - 1;
//            start_pos = scroll * SCROLL_INCREMENT;
//          }
//          buttonRight.Enabled = false;
        }

        double scale = (width * 0.98) / (double)max_len;

        if (scroll > 0)
          buttonLeft.Enabled = true;

        // if there is only one time series to plot
        if (alData2.Count == 0)
        {
          
          if (plot_scale > scale)
          {
            end_pos = (int)(start_pos + width / plot_scale + 1);
            buttonRight.Enabled = true;     
            //      buttonZoomOut.Enabled = true;

            if (end_pos >= alData.Count)
              //        if (end_pos >= data.Count)
            {
//              width *= 0.98;

              end_pos = alData.Count - 1;
              buttonRight.Enabled = false;
            }
          }

          else
          {
          //          width *= 0.98;  
          //          plot_scale = width / (double)alData.Count;
            end_pos = alData.Count - 1;
            buttonRight.Enabled = false;
            buttonZoomOut.Enabled = false;
          }
        } 

        // if there are two time series to plot
        else
        {
//          if (scale < plot_scale)
//            plot_scale = scale;

          end_pos = (int)(start_pos + width / plot_scale - 1);

          if (end_pos >= max_len)
            //        if (end_pos >= data.Count)
          {
//            width *= 0.98;

            end_pos = max_len - 1;
            buttonRight.Enabled = false;
//            buttonZoomOut.Enabled = false;
          }

          else
          {
            buttonRight.Enabled = true;
            buttonZoomOut.Enabled = true;
          }

        }     
        

        int interval = 0;

        if (scroll == 0 && (SCROLL_INCREMENT < ((end_pos - start_pos + 1)/4)))
          interval = (end_pos -start_pos+1) / 4;
        else
          interval = SCROLL_INCREMENT;

        g.DrawLine(new Pen(Color.Black, 1), new PointF((float)0, (float)height), new PointF((float)width, (float)height));  

        for (int i = start_pos; i <= end_pos; i++)
        {
          if ( ((i-start_pos) % interval) == 0 || i == end_pos)
          {
            if ( (i-start_pos) != 0)
            {
              // if the last tick is too close to the last point, then don't display it
              if ( i == end_pos || ((end_pos - i) > (interval/2)) )
              {
                g.DrawLine(new Pen(Color.Black, 1), 
                  new PointF((float)((i-start_pos)*plot_scale), (float)(height-5)), 
                  new PointF((float)((i-start_pos)*plot_scale), (float)(height+5)));  
        
                g.DrawString(Convert.ToString(i), Font, Brushes.Black, (float)((i-start_pos)*plot_scale-10), (float)(height+5));
              }
            }

            else if ( (i-start_pos) == 0 )
              g.DrawString(Convert.ToString(i), Font, Brushes.Black, (float)0, (float)(height+5));

          }
        }

        //ArrayList dummy = new ArrayList();

      //  SCROLL_INCREMENT = alData.Count / 10;

        if (radioViewDiff.Checked)
        {
          int count1, count2;

          count2 = (highlight.Count - weight) / 2;
          count1 = highlight.Count - count2;

          ArrayList highlight1 = new ArrayList();
          ArrayList highlight2 = new ArrayList();

          for (int i = 0; i < count1; i++)
          {
            highlight1.Add(highlight[i]);
          }

          for (int i = count1; i < highlight.Count; i++)
          {
            highlight2.Add(highlight[i]);
          }

          Pen pen2 = new Pen(Color.Green, 1);

          PlotTimeSeries(alData, highlight1, width, height, g, pen1, 1, start_pos, end_pos);
          PlotTimeSeries(alData2, highlight2, width, height, g, pen2, 2, start_pos, end_pos);

          return;
        }

        // Plot the time series first
        if (radioViewFirst.Checked)
          PlotTimeSeries(alData, highlight, width, height, g, pen1, 1, start_pos, end_pos);
        else
          PlotTimeSeries(alData, new ArrayList(), width, height, g, pen1, 1, start_pos, end_pos);

        if (added_second)
        {
          Pen pen2 = new Pen(Color.Green, 1);

          if (radioViewSecond.Checked)
            PlotTimeSeries(alData2, highlight, width, height, g, pen2, 2, start_pos, end_pos);
          else
            PlotTimeSeries(alData2, new ArrayList(), width, height, g, pen2, 2, start_pos, end_pos);
        }
      }

      private void PlotTimeSeries(ArrayList data, ArrayList highlight,
                      double width, double height, Graphics g, Pen p, int index,
                    int start_pos, int end_pos)
      {
//        double scale = width / (double)data.Count;


        if (start_pos >= data.Count)
          return;

        if (end_pos >= data.Count)
          end_pos = data.Count - 1;

        // we don't allow zoom-in of larger than 10% of the data
        //if ((end_pos - start_pos) < alData.Count/10)
        //  buttonZoomIn.Enabled = false;

        double dblNewPos = 0.0;

        double dblMax = GetMax(data);

        double dblMin = GetMin(data);

        dblMax += dblMax * 0.05;

        // Leave 5% space.. This check is to make sure that the new Min is actually
        // smaller than the true Min
        if (dblMin > 0)
          dblMin -= dblMin * 0.05;
        else
          dblMin += dblMin * 0.05;

        double dblDiff = dblMax - dblMin;

//        double zeroPos = (0 - dblMin)/dblDiff * height;
//        g.DrawLine(new Pen(Color.Black, 1), new PointF(0, (float)zeroPos), 
//          new PointF((float)width, (float)zeroPos));


        double dblNewPos2 = 0.0;

        // get the position of the first point
        //    dblNewPos = iwinHeight - (dtsData[0]/ dblDiff) * iwinHeight;
        dblNewPos = height - (((double)data[start_pos] - dblMin)/dblDiff) * height;

        using (p)
        {
          for (int i = start_pos + 1; i <= end_pos; i++)
          {
            dblNewPos2 = height - (((double)data[i] - dblMin)/(dblMax-dblMin)) * height;
            g.DrawLine(p, new PointF((float)((i-start_pos-1)*plot_scale), (float)dblNewPos),
              new PointF((float)((i-start_pos)*plot_scale), (float)dblNewPos2));

            dblNewPos = dblNewPos2;
          }
        }


        if (highlight.Count > 0)
        {
          int cur_offset = 0;

          Pen highlight_pen;
          
          if (radioViewDiff.Checked)
          {
            if (index == 1)
              highlight_pen = new Pen(Color.Red, 5);
            else
              highlight_pen = new Pen(Color.Brown, 5);
          }

          else
            highlight_pen = new Pen(Color.Red, 5);

//          using (Pen redpen = new Pen(Color.Red, 2))
//          {
          for (int i = 0; i < highlight.Count; i++)
          {
            cur_offset = (int)highlight[i];

            // check if this subsequence is among the plotted subsequences
            if (cur_offset >= start_pos && cur_offset <= end_pos)           
            { 
              dblNewPos = height - (((double)data[cur_offset] - dblMin)/dblDiff) * height;

              for (int j = 1; j < window_len; j++)
              {
                dblNewPos2 = height - (((double)data[cur_offset+j] - dblMin)/(dblMax-dblMin)) * height;
                g.DrawLine(highlight_pen, new PointF((float)((cur_offset+j-start_pos-1)*plot_scale), (float)dblNewPos),
                  new PointF((float)((cur_offset+j-start_pos)*plot_scale), (float)dblNewPos2));

                dblNewPos = dblNewPos2;
              }
            }
          }
        }
      }



      private double GetMax(ArrayList data)
      {
        double max = (double)data[0];

        for (int i = 1; i < data.Count; i++)
        {
          if ((double)data[i] > max)
            max = (double)data[i];
        }

        return max;
      }

      private double GetMin(ArrayList data)
      {
        double min = (double)data[0];

        for (int i = 1; i < data.Count; i++)
        {
          if ((double)data[i] < min)
            min = (double)data[i];
        }
        return min;

      }

      private double GetMax(double[] data)
      {
        double max = data[0];

        for (int i = 1; i < data.Length; i++)
        {
          if (data[i] > max)
            max = data[i];
        }

        return max;
      }

      private double GetMin(double[] data)
      {
        double min = data[0];

        for (int i = 1; i < data.Length; i++)
        {
          if (data[i] < min)
            min = data[i];
        }
        return min;

      }

      // Boolean flag used to determine when a character other than a number is entered.
      private bool nonNumberEntered = false;

      private bool CheckNumeric(System.Windows.Forms.KeyEventArgs e)
      {
        // Initialize the flag to false.
        bool isNumeric = true;

        // Determine whether the keystroke is a number from the top of the keyboard.
        if (e.KeyCode < Keys.D0 || e.KeyCode > Keys.D9)
        {
          // Determine whether the keystroke is a number from the keypad.
          if (e.KeyCode < Keys.NumPad0 || e.KeyCode > Keys.NumPad9)
          {
            // Determine whether the keystroke is a backspace.
            if(e.KeyCode != Keys.Back)
            {
              // A non-numerical keystroke was pressed.
              // Set the flag to true and evaluate in KeyPress event.
              isNumeric = false;
            }
          }
        }

        return isNumeric;
      }

      // Handle the KeyDown event to determine the type of character entered into the 
      // control.
      private void txtWindowLen_KeyDown(object sender, 
        System.Windows.Forms.KeyEventArgs e)
      {
        if (!CheckNumeric(e))
          nonNumberEntered = true;
        else
          nonNumberEntered = false;

      }

      // This event occurs after the KeyDown event and can be used to prevent
      // characters from entering the control.
      private void txtWindowLen_KeyPress(object sender, 
        System.Windows.Forms.KeyPressEventArgs e)
      {
        // Check for the flag being set in the KeyDown event.
        if (nonNumberEntered == true)
        {
          // Stop the character from being entered into the control since it is 
          // non-numerical.
          e.Handled = true;
        }
      }

      // Handle the KeyDown event to determine the type of character entered into the 
      // control.
      private void txtNumSeg_KeyDown(object sender, 
        System.Windows.Forms.KeyEventArgs e)
      {
        if (!CheckNumeric(e))
          nonNumberEntered = true;
        else
          nonNumberEntered = false;

      }

      // This event occurs after the KeyDown event and can be used to prevent
      // characters from entering the control.
      private void txtNumSeg_KeyPress(object sender, 
        System.Windows.Forms.KeyPressEventArgs e)
      {
        // Check for the flag being set in the KeyDown event.
        if (nonNumberEntered == true)
        {
          // Stop the character from being entered into the control since it is 
          // non-numerical.
          e.Handled = true;
        }
      }

      private void buttonShowTree_Click(object sender, System.EventArgs e)
      {
        this.Cursor = Cursors.WaitCursor;

        window_len = Convert.ToInt16(txtWindowLen.Text);


        if (window_len > alData.Count)
        {
          MessageBox.Show ("Sliding window length cannot exceed the length of the time series", "My Application", 
            MessageBoxButtons.OK, MessageBoxIcon.Asterisk);
          this.Cursor = Cursors.Default;
          return;
        }

        num_seg = Convert.ToInt16(txtNumSeg.Text);

        REDRAW = false;

        NR_option = Op.NR_OPT;
        NORM = Op.NORM;

        alphabet_size = 
          Convert.ToByte(comboAlphabet.GetItemText(comboAlphabet.SelectedItem));
              
        s.WindowLen = window_len;

        int[] arOffset = new int[0];

        ArrayList alSymbol = s.BuildMatrix(alData, ref arOffset, alphabet_size, num_seg, NORM, 
                           NR_option);

        total_weight = alSymbol.Count;

        if (radioViewFirst.Checked)
        {
          BuildTree(alSymbol, arOffset);
        }
          // if there are two time series
        else
        {
          if (added_second && alData2.Count > 0)
          {
            s2.WindowLen = window_len;
            int[] arOffset2 = new int[0];
            ArrayList alSymbol2 = s2.BuildMatrix(alData2, ref arOffset2, alphabet_size, num_seg,
              NORM, NR_option);

            total_weight2 = alSymbol2.Count;

            if (radioViewSecond.Checked)
              BuildTree(alSymbol2, arOffset2);
            else if (radioViewDiff.Checked)
              BuildTree(alSymbol, alSymbol2, arOffset, arOffset2);
          }
        }

        surprising.Clear();
        scores.Clear();

//        t.GetMaxWeight();
//        if (radioViewFirst.Checked == true)
//        {
          DrawTree(t.Root, pbTree);
//        }

//        else if (radioViewSecond.Checked == true)
//        {
//          DrawTree(t2.Root, pbTree);
//        }

        this.Cursor = Cursors.Default;
      }

      public void BuildTree(ArrayList alSymbol, int[] arOffset)
      {
        byte[] temp;

        t = new Tree(alphabet_size, num_seg);

        // Insert one string at a time
        for (int i = 0; i < alSymbol.Count; i++)
        {
          temp = (byte[])alSymbol[i];
          t.Insert(temp, arOffset[i]);
        }

      }

      public void BuildTree(ArrayList alSymbol, ArrayList alSymbol2, 
                    int[] arOffset, int[] arOffset2)
      {
        byte[] temp;

        t = new Tree(alphabet_size, num_seg);

        Console.WriteLine("Build first tree");

        // Insert strings from the first dataset
        for (int i = 0; i < alSymbol.Count; i++)
        {
          temp = (byte[])alSymbol[i];
          t.Insert(temp, arOffset[i]);
        }

        Console.WriteLine("Build second tree");

        t.Predict(alSymbol.Count, alSymbol2.Count);

        // Insert strings from the second dataset
        for (int i = 0; i < alSymbol2.Count; i++)
        {
          temp = (byte[])alSymbol2[i];
          t.InsertDiff(temp, arOffset2[i]);
        }
    //    Console.WriteLine("Max Weight: {0}", Convert.ToString(t.GetMaxWeight()));
      }

      private void PlotSubsequences(Node n, int child_index, ArrayList data)
      {
        pbSubseq.Image = new Bitmap(pbSubseq.Width, pbSubseq.Height);

        // make sure the size is not 0
        if (pbSubseq.Width <= 0 | pbSubseq.Height <= 0)
          return;

        // get image properties
        Image im = pbSubseq.Image;
        double width = (pbSubseq.Width * 0.98);
        double height = (pbSubseq.Height * 0.9);
        Graphics g = Graphics.FromImage( pbSubseq.Image );

        double scale = (double)width / (double)window_len;

        double dblNewPos;// = 0.0;
//        double[] temp_data = new double[data.Count];
//        double[] temp_data2;

//        data.CopyTo(temp_data, 0);
//        temp_data = Normalize(temp_data);
              
//        double dblMax = GetMax(temp_data);
//        double dblMin = GetMin(temp_data);

        double dblMax = 0;
        double dblMin = 0;
        double dblDiff = 0;
        double dblDiff2 = 0;

        double dblMax2 = 0;
        double dblMin2 = 0;

        double cur_max = 0;
        double cur_min = 0;

  /*      dblMax += dblMax * 0.05;

        // Leave 5% space.. This check is to make sure that the new Min is actually
        // smaller than the true Min
        if (dblMin > 0)
          dblMin -= dblMin * 0.05;
        else
          dblMin += dblMin * 0.05;*/

//        double dblDiff = dblMax - dblMin;

//        double zeroPos = (0 - dblMin)/dblDiff * height;
        
        
//        g.DrawLine(new Pen(Color.Black, 1), new PointF(0, (float)zeroPos), 
//          new PointF((float)width, (float)zeroPos));

        // get the position of the first point
        //    dblNewPos = iwinHeight - (dtsData[0]/ dblDiff) * iwinHeight;
//        dblNewPos = height - (((double)alData[alOffset[0]] - dblMin)/dblDiff) * height;

        double dblNewPos2 = 0.0;
        int cur_offset = 0;

        double[] norm;
        double[] temp = new double[window_len]; 

        ArrayList alNorm = new ArrayList();
        int count1 = 0;
        int count2 = 0;

        Pen bluepen = new Pen(Color.Blue, 1);
        Pen greenpen = new Pen(Color.Green, 1);

    //    using (Pen bluepen = new Pen(Color.Blue, 1))
    //    {
          if (radioViewDiff.Checked)
          {

            count2 = (n.children[child_index].offset.Count - n.children[child_index].weight) / 2;
            count1 = n.children[child_index].offset.Count - count2;

              // first sequence
              for (int i = 0; i < count1; i++)
              {
                cur_offset = (int)n.children[child_index].offset[i];
              
                Console.WriteLine(Convert.ToString(cur_offset));

                // Copy the subsequence into another array
                //Array.Copy(data, cur_offset, temp, 0, window_len);
                data.CopyTo(cur_offset, temp, 0, window_len);
                norm = Normalize(temp);
                alNorm.Add(norm);

                if (i == 0)
                {
                  dblMax = GetMax(norm);
                  dblMin = GetMin(norm);
                }

                else
                {
                  if ( (cur_max = GetMax(norm)) > dblMax )
                    dblMax = cur_max;

                  if ( (cur_min = GetMin(norm)) < dblMin )
                    dblMin = cur_min;
                }


              }

            dblDiff = dblMax - dblMin;
            if ( dblDiff < 0.0000000001)
              dblDiff = 1;

            for (int i = 0; i < count2; i++)
            {
              cur_offset = (int)n.children[child_index].offset[i+count1];
            
              Console.WriteLine(Convert.ToString(cur_offset));

              // Copy the subsequence into another array
              //Array.Copy(data, cur_offset, temp, 0, window_len);
              alData2.CopyTo(cur_offset, temp, 0, window_len);
              norm = Normalize(temp);
              alNorm.Add(norm);

              if (i == 0)
              {
                dblMax2 = GetMax(norm);
                dblMin2 = GetMin(norm);
              }

              else
              {
                if ( (cur_max = GetMax(norm)) > dblMax2 )
                  dblMax2 = cur_max;

                if ( (cur_min = GetMin(norm)) < dblMin2 )
                  dblMin2 = cur_min;
              }
            }


          }

          else
          {
            // Normalize data and find the min and max
            for (int i = 0; i < n.children[child_index].weight; i++)
            {
              cur_offset = (int)n.children[child_index].offset[i];

              Console.WriteLine(Convert.ToString(cur_offset));

              // Copy the subsequence into another array
              //Array.Copy(data, cur_offset, temp, 0, window_len);
              data.CopyTo(cur_offset, temp, 0, window_len);
              norm = Normalize(temp);
              alNorm.Add(norm);

              if (i == 0)
              {
                dblMax = GetMax(norm);
                dblMin = GetMin(norm);
              }

              else
              {
                if ( (cur_max = GetMax(norm)) > dblMax )
                  dblMax = cur_max;

                if ( (cur_min = GetMin(norm)) < dblMin )
                  dblMin = cur_min;
              }
            }

            // Leave 5% space.. This check is to make sure that the new Min is actually
            // smaller than the true Min
            if (dblMin > 0)
              dblMin -= dblMin * 0.05;
            else
              dblMin += dblMin * 0.05;

            dblMax += dblMax * 0.05;

            dblDiff = dblMax - dblMin;

            if (dblDiff < 0.0000000001)
              dblDiff = 1;
          }

          

          for (int i = 0; i < n.children[child_index].offset.Count; i++)
          {
            temp = (double[])alNorm[i];
            
            if (radioViewDiff.Checked && i >= count1)
            {
              dblDiff2 = dblMax2 - dblMin2;

              if (dblDiff2 < 0.00000000001)
                dblDiff2 = 1;

              dblNewPos = height/2 - (((double)temp[0] - dblMin2)/dblDiff2) * height/2 + height/2;

              for (int j = 1; j < window_len; j++)
              {
                dblNewPos2 = height/2 - (((double)temp[j] - dblMin2)/dblDiff2) * height/2 + height/2;
                g.DrawLine(greenpen, new PointF((float)((j-1)*scale), (float)dblNewPos),
                  new PointF((float)(j*scale), (float)dblNewPos2));

                dblNewPos = dblNewPos2;
              }
            }

            else if (radioViewDiff.Checked && i < count1)
            {
              dblDiff = dblMax - dblMin;
              if (dblDiff < 0.0000000001)
                dblDiff = 1;

              dblNewPos = height/2 - (((double)temp[0] - dblMin)/dblDiff) * height/2;

              for (int j = 1; j < window_len; j++)
              {
                dblNewPos2 = height/2 - (((double)temp[j] - dblMin)/dblDiff) * height/2;
                g.DrawLine(bluepen, new PointF((float)((j-1)*scale), (float)dblNewPos),
                  new PointF((float)(j*scale), (float)dblNewPos2));

                dblNewPos = dblNewPos2;
              }
            }

            else
            {
              dblNewPos = height - (((double)temp[0] - dblMin)/dblDiff) * height;

              for (int j = 1; j < window_len; j++)
              {
                dblNewPos2 = height - (((double)temp[j] - dblMin)/dblDiff) * height;
                g.DrawLine(bluepen, new PointF((float)((j-1)*scale), (float)dblNewPos),
                  new PointF((float)(j*scale), (float)dblNewPos2));

                dblNewPos = dblNewPos2;
              }
            }
          }
        //}

        curOffset = n.children[child_index].offset;
        curWeight = n.children[child_index].weight;
//        if (!radioViewDiff.Checked)
          Plot(curOffset, curWeight);

//        Plot(n.children[child_index].offset, n.children[child_index].weight);

      }

      private bool REDRAW;
      private bool ZOOM_IN_ORIGINAL;

      private void Refresh(PictureBox myPB)
      {
        myPB.Image = new Bitmap((int)myPB.Width, (int)myPB.Height);

        // get image properties
        Image im = myPB.Image;
        Graphics g = Graphics.FromImage( myPB.Image );
        g.Clear(Color.White);
      }

      private void DrawTree(Node r, PictureBox myPB)
      {
        double width = (myPB.Width);
        double height = (double)(myPB.Height);

    //    NR_option = Op.NR_OPT;

        
        int max_line_width = 8;
//        int max_weight = 0;//t.GetMaxWeight();

        int[] max_weight = t.MaxWeight;

        double line_width;

        int cur_node_depth = r.depth;

        // make sure the size is not 0
        if (width <= 0 | height <= 0)
          return;

        myPB.Image = new Bitmap((int)width, (int)height);

        width *= 0.9;

        // get image properties
        Image im = myPB.Image;
        Graphics g = Graphics.FromImage( myPB.Image );

        g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.AntiAlias;

        Node curNode;
        ArrayList alQueue = new ArrayList();

        r.x_tmp = root_pos;
        r.y_tmp = (double)height / (double)2;

        if (radioViewDiff.Checked)
        {
          g.DrawLine(new Pen(Brushes.Blue, 2f), 5, 10, 20, 10);
          g.DrawString("Underrepresented", Font, 
                Brushes.Blue, 25, 5);
          g.DrawLine(new Pen(Brushes.Green, 2f), 5, 25, 20, 25);
          g.DrawString("Overrepresented", Font, Brushes.Green, 25, 20);
          g.DrawLine(new Pen(Brushes.Red, 2f), 5, 40, 20, 40);
          g.DrawString("Ranked Surprising Patterns", Font, Brushes.Red, 25, 35);
        }

        int total_w;


        if (radioViewFirst.Checked)
          total_w = total_weight;
        else if (radioViewSecond.Checked) 
          total_w = total_weight2;
        else
        {
          if (total_weight > total_weight2)
            total_w = total_weight;
          else
            total_w = total_weight2;
        }

        double min_freq = 1 / (double)total_w;
        

        int surprising_count = 0;
        int surprising_pos = 0;

        double freq1 = 0.0;
        double freq2 = 0.0;

        double surprise_factor = 0.0;

        double temp_score = 0.0;

        int weight = 0;

        // This will be the thickest line possible
        double log_min_freq = 0 - Math.Log(min_freq);

        if (!REDRAW)
        {
          r.x = r.x_tmp;
          r.y = r.y_tmp;
        }
        
        alQueue.Insert(0, r);
        
        int node_count = 1;

        // index of a node at the current level
        int index = 1;
        //      byte level = 0;
        int cur_level_node = 1;
        int cur_depth = -1;
        double grid_width = (double)width / (num_seg - r.depth);
        double grid_height = 0.0;

        double intensity = 0.0;

        int count1 = 0;
        int count2 = 0;
        int cur_max = 0;

        double overlap = 0.0;

        byte[] path = new byte[num_seg];

        while (alQueue.Count > 0)
        {
          curNode = (Node)alQueue[0];

          for (byte i = 0; i < alphabet_size; i++)
          {
            if (curNode.children[i] == null && curNode.depth - r.depth < num_seg - r.depth)
            {
              Node dummy = new Node(alphabet_size, (byte)(i+1), 
                (byte)(curNode.depth+1));
              
              if (curNode.depth-r.depth+1 != cur_depth)
              {
                index = 1;              
                cur_level_node = (int)Math.Pow(alphabet_size, curNode.depth-r.depth+1); 
                //cur_depth++;
                cur_depth = curNode.depth + 1 - r.depth;
                grid_height = (double)(height) / (double)(cur_level_node+1);

                dummy.x_tmp = grid_width * (curNode.depth+1-r.depth);
                dummy.y_tmp = index * grid_height;

                g.DrawLine(new Pen(Brushes.Gray, 0.5f),
                  (float)(dummy.x_tmp), 0.0f, 
                  (float)(dummy.x_tmp), 
                  (float)height); 
              }
              
              else
              {
                dummy.x_tmp = grid_width * (curNode.depth+1-r.depth);
                dummy.y_tmp = index * grid_height;
              }

              dummy.x = dummy.x_tmp;
              dummy.y = dummy.y_tmp;

          //    if (REDRAW)
          //    {
                g.DrawLine(new Pen(Brushes.Gray, 0.1f),//curNode.children[i].weight), 
                  (float)(curNode.x_tmp), (float)curNode.y_tmp, 
                  (float)dummy.x_tmp, 
                  (float)dummy.y_tmp);  
          //    }

              alQueue.Insert(alQueue.Count, (Node)dummy);
              index++;  
            }

            else if (curNode.children[i] != null)
            {
              if (curNode.children[i].depth - r.depth != cur_depth)
              {
                index = 1;
                cur_depth = curNode.children[i].depth - r.depth;
                cur_level_node = (int)Math.Pow(alphabet_size, curNode.depth-r.depth+1);
                grid_height = (double)height / (double)(cur_level_node+1);

                curNode.children[i].x_tmp = grid_width * (curNode.children[i].depth-r.depth);
                curNode.children[i].y_tmp = index * grid_height;

                g.DrawLine(new Pen(Brushes.Gray, 0.5f),
                  (float)(curNode.children[i].x_tmp), 0.0f, 
                  (float)(curNode.children[i].x_tmp), 
                  (float)height);   
              }

              else
              {
                curNode.children[i].x_tmp = grid_width * (curNode.children[i].depth-r.depth);
                curNode.children[i].y_tmp = index * grid_height;
              }

              if (!REDRAW)
              {
                curNode.children[i].x = curNode.children[i].x_tmp;
                curNode.children[i].y = curNode.children[i].y_tmp;
              }

              if (radioViewDiff.Checked)
              {
                // use expected value -- it did not exist in the reference dataset
                if (curNode.children[i].expected < 0)
                {
                  weight = Math.Abs(curNode.children[i].expected) - Math.Abs(curNode.children[i].weight);

                  // get the expected value
                  count1 = (int)Math.Abs(curNode.children[i].expected);
                  freq1 = (double)count1 / (double) total_weight;
                  
                  // get the observed value in the test data
                  count2 = curNode.children[i].offset.Count;
                  freq2 = (double)count2 / (double)total_weight2;

                  if (curNode.children[i].weight > 0)
                  {
                    if (freq1 > freq2)
                      overlap = freq2 / freq1;
                    else
                      overlap = freq1 / freq2;
                  }
                  else
                  {
                    overlap = 0;
                  }

                  cur_max = (count2 > count1 ? count2 : count1);
    
                  surprise_factor = (double)weight / (double)cur_max;

                  //line_width = surprise_factor * max_line_width; //(double)cur_max * 20;
                  line_width = (1 - overlap) * (-1) * max_line_width;
                }

                else if (curNode.children[i].expected == 0 && curNode.children[i].weight != 0)
                {
                  surprise_factor = (0.01 - Math.Abs(curNode.children[i].weight)) / Math.Abs(curNode.children[i].weight);
                //  line_width = surprise_factor * max_line_width; //(double)cur_max * 20;
                  line_width = (-0.99) * max_line_width;
                }

                // use actual value
                else
                { 
                  count2 = (curNode.children[i].offset.Count - curNode.children[i].weight) / 2;
                  count1 = curNode.children[i].offset.Count - count2;
    
                  if (count2 == 0)
                    count2 = 1;

                  freq1 = (double)count1 / (double)total_weight;
                  freq2 = (double)count2 / (double)total_weight2;

                  cur_max = (count2 > count1 ? count2 : count1);
    
                  //  line_width = (double)curNode.children[i].weight / (double)cur_max * max_line_width;

                  if (freq1 > freq2)
                    overlap = freq2 / freq1;
                  else
                    overlap = freq1 / freq2;

                  if (curNode.children[i].weight != 0)
                  {
                    surprise_factor = (double)curNode.children[i].weight / (double)cur_max;
                    line_width = surprise_factor * max_line_width;
                    line_width = (1 - overlap) * max_line_width;

                  }

                  else
                  {
                    if (curNode.children[i].offset.Count - curNode.children[i].weight == 0)
                      overlap = 0.01;
                      //surprise_factor = 0.01;
                    else  
                      overlap = (double)curNode.children[i].weight / (double)cur_max;
                      
                    //  surprise_factor = ((double)curNode.children[i].offset.Count/2) / (double)total_w;
                    line_width = surprise_factor * 20;
                //    line_width = (1 - overlap) * max_line_width;
                  }
                }

                surprise_factor = 1 - overlap;

                if (!REDRAW && (curNode.children[i].depth == num_seg))
                {
                  if (curNode.children[i].weight != 0)
                  {
                    //surprise_factor *= overlap;
                    temp_score = Math.Abs(surprise_factor);
                    path = GetPath(curNode.children[i]);
                    InsertSurprise(temp_score, ref scores, ref surprising, path);
                  }
                }

            

              }

              else
              {
                line_width = (double)curNode.children[i].weight / (double)total_w * 20;
              }

              alQueue.Insert(alQueue.Count, (Node)curNode.children[i]);
              node_count++;
              index++;

    
              Pen p;
   
              if ((double)curNode.children[i].weight < 0)
              {
                intensity = (double)(Math.Abs(curNode.children[i].weight))/(double)total_w * 255;

                Color c = Color.FromArgb(0, Math.Abs(255-(int)intensity), Math.Abs((int)intensity));

                if (line_width < 0)
                  p = new Pen(c, (float)(0-line_width));
                else
                  p = new Pen(c, (float)(line_width));
//                p = new Pen(Brushes.Green, (float)(0-line_width));
              }

              else if ((double)curNode.children[i].weight > 0)
              {
                if (!radioViewDiff.Checked)
                {
                  intensity = (double)(Math.Sqrt((Math.Abs(curNode.children[i].weight))/(double)total_w)) * 255;

                  Color c = Color.FromArgb(0, Math.Abs((int)intensity), Math.Abs(255-(int)intensity));

                  p = new Pen(Brushes.Red, (float)(line_width));
                }

                else
                  p = new Pen(Brushes.Blue, (float)(line_width));
              }

              else
              {
                if (curNode.children[i].offset.Count == 0)
                  p = new Pen(Brushes.Gray, 0.1f);
                else
                {
                  Color c = Color.FromArgb(50, 122, 122);
                  p = new Pen(c, (float)(line_width));
                }
              }

              g.DrawLine(p, 
                  (float)(curNode.x_tmp), (float)curNode.y_tmp, 
                  (float)(curNode.children[i].x_tmp), 
                  (float)curNode.children[i].y_tmp);            

            }



          }
          alQueue.RemoveAt(0);

        }

        Pen p2 = new Pen(Brushes.Red, 0.5f);
        
        Node NextNode;

        bool EXCLUDE = false;

        if (radioViewDiff.Checked)// && !REDRAW)
        {
          for (int i = 0; i < surprising.Count; i++)
          {
            EXCLUDE = false;

            path = (byte[])surprising[i];

            curNode = r;
            
            // for the redraw window, we have to verify first if the surprising pattern exists in the subtree
            // (if not then we don't need to draw it)
            if (REDRAW)
            {
              Node temp = r;

              // see if the current surprising path is in the subtree (by matching all the nodes from the root to the 
              // current node)
              for (int a = r.depth; a > 0; a--)
              {
                if (temp.data != path[a-1])
                {
                  EXCLUDE = true;
                  break;
                }

                temp = temp.parent;
              }
            }           
              
            if (!REDRAW || !EXCLUDE) //&& r.data == path[r.depth-1]))
            {
              for (int j = r.depth; j < num_seg; j++)
              {
                NextNode = curNode.children[path[j]-1];

                g.DrawLine(p2, 
                  (float)(curNode.x_tmp), (float)curNode.y_tmp, 
                  (float)(NextNode.x_tmp), 
                  (float)NextNode.y_tmp);           

                if (j == num_seg - 1)
                  g.DrawString(Convert.ToString(i+1), new Font(Font, FontStyle.Bold), Brushes.Black, (float)NextNode.x_tmp, (float)(NextNode.y_tmp));

                curNode = NextNode;
      
              }
            }
          }
        }


        //double plot_scale = width / (double)alData.Count;
      }

      private byte[] GetPath(Node curNode)
      {
        byte[] path = new byte[curNode.depth];

        for (int i = curNode.depth-1; i >= 0; i--)
        {
          path[i] = curNode.data;
          curNode = curNode.parent;
        }

        return path;
      }

      
      private void InsertSurprise(double cur_score, ref ArrayList score, ref ArrayList surprising, byte[] path)
      {
        int pos = 0;

        if (score.Count > 0)
        {
          // check to see if it's smaller than everything else
          if (cur_score < (double)score[score.Count-1])
          {
            if (score.Count < MAX_SURPRISE)
              pos = score.Count;
            else
              return;
          }

          // find out where to insert the surprising pattern
          if (cur_score < (double)score[0])
          {
            for (int i = 0; i < surprising.Count; i++)
            {
              if (cur_score >= (double)score[i])
              {
                pos = i;
                break;
              }
            }
          }
        }
        
        if (score.Count >= MAX_SURPRISE)
        {
          surprising.RemoveAt(score.Count-1);
          score.RemoveAt(score.Count-1);
        }

        surprising.Insert(pos, path);
        score.Insert(pos, cur_score);

      }

      // Given the x-coordinate of mouse click, find out the current depth of the click
      private int GetDepth(int x, int width, int max_depth)
      {
        int w = (int)(width * 0.9);
//        int w = (int)(pbTree.Width * 0.9);
        double grid_width = (double)w / (double)max_depth;

        int cur_depth = 0;
      //  double distance_to_node;

        if (x < 20)
          cur_depth = 0;
    
          /*      else
                {
                  cur_depth = (int)Math.Floor((x / grid_width));
          
                  distance_to_node = Math.Abs(x - cur_depth * grid_width);

                  // if the clicked location is 10% grid-size away from the nodes in the
                  // current depth, then the mouse is considered not on the node
                  if (distance_to_node / grid_width > 0.1)
                    cur_depth = 0 - cur_depth;
                }

                return cur_depth;*/

          //      Node curNode = t.Root;


        else
        {
          double cur_x = 0.0;
          double next_x = 0.0;

          cur_x = grid_width;

          for (int i = 1; i < max_depth; i++)
          {
            next_x = cur_x + grid_width;
    
            if (x > cur_x * 0.98 && x < next_x * 1.02)
            {
              if (x > cur_x * 0.98 && x < cur_x * 1.02)
              {
                cur_depth = i;
                break;
              }

              else if (x > next_x * 0.98 && x < next_x * 1.02)
              {
                cur_depth = i+1;
                break;
              }

              else
              {
                cur_depth = 0 - i;
                break;
              }
            }
            cur_x = next_x;
          }
        }

        return cur_depth;
      }

      private int GetNodeIndex(int cur_depth, int x, int y, int width, int height, int max_depth)
      {
//        h = pbTree.Height;

//        int cur_depth = GetDepth(x);
        int num_node = (int)Math.Pow(alphabet_size, cur_depth);
        int node_index = 0;

        double grid_height = (double)height / (double)(num_node+1);
        double grid_width = (double)(width*0.9) / (double)max_depth;


        node_index = (int)Math.Round(y / grid_height);

        double distance_to_node_x;

        if (cur_depth > 0)
          distance_to_node_x = Math.Abs(x - cur_depth * grid_width);

        // root's position is hard-coded
        else
          distance_to_node_x = Math.Abs(x - root_pos);

        double distance_to_node_y = Math.Abs(y - node_index * grid_height);
        
        // The tolerance should increase with depth because at higher depth
        // the spacings are smaller
        double threshold = 0.05;

        if (!ZOOM_IN_ORIGINAL)
          threshold = 0.02;

        if (distance_to_node_y / grid_height > threshold ||
          distance_to_node_x / grid_width > threshold) //(0.1 + 2 * cur_depth))
          node_index = -1;

        return node_index;
      }

      // 
      private Node GetParent(int depth, int y, int height)
      {
        int num_node = (int)(Math.Pow(alphabet_size, depth+1));

        double grid_height = (double)height / (double)(num_node + 1);

        int parent_index = 0;

        double max_y = 0.0;

        for (int i = 0; i < (int)(Math.Pow(alphabet_size, depth)); i++)
        {
          max_y = alphabet_size * (i+1) * grid_height;

          if (y <= (int)Math.Ceiling(max_y))
          {
            parent_index = i+1;
            break;
          }
        }

        byte[] path = GetPath(depth, parent_index);
        
        if (pathZoomedIn.Length > 0 && !ZOOM_IN_ORIGINAL)
        {
          byte[] temp = new byte[pathZoomedIn.Length + path.Length];

          if (pathZoomedIn[0] > 0)
          {
            for (int i = 0; i < pathZoomedIn.Length; i++)
            {
              temp[i] = pathZoomedIn[i];
            }
        
            for (int i = 0; i < path.Length; i++)
            {
              temp[i+pathZoomedIn.Length] = path[i];
            }

            path = temp;
          
//            pathZoomedIn = temp;
          }

//          else
//            temp = path;
        }

//        else
//        {
//          temp = path;
//        }

//        if (radioViewFirst.Checked && t != null)
          return t.GetNode(path);     
//        else if (radioViewSecond.Checked && t2 != null)
//          return t2.GetNode(path);
//        else
//          return t.GetNode(path);
      }

      private int GetClosestBranch(Node parentNode, int x, int y)
      {
        double min_dist = 0.0;
        double perp_dist = 0.0;
        int child_index = 0;
        bool FIRST = true;

        for (int i = 0; i < alphabet_size; i++)
        {
          if (parentNode.children[i] != null)
          {
            if (ZOOM_IN_ORIGINAL)
            {
              perp_dist = GetPerpDistance(parentNode.x, parentNode.y, parentNode.children[i].x,
                parentNode.children[i].y, x, y);
            }

            else
            {
              perp_dist = GetPerpDistance(parentNode.x_tmp, parentNode.y_tmp,
                parentNode.children[i].x_tmp, parentNode.children[i].y_tmp, x, y);
            }

            if (FIRST)
            {
              min_dist = perp_dist;
              child_index = i;
              FIRST = false;
            }

            else
            {
              if (perp_dist < min_dist)
              {
                min_dist = perp_dist;
                child_index = i;
              }
            }
          }
        }
        
        return child_index;     
      }

      private byte[] GetPath(int depth, int node_index)
      {
        byte[] path = new byte[depth];

        if (depth == 1)
          path[0] = (byte)node_index;

        else
        {
          // back-trace for the path
          for (byte i = (byte)depth; i > 0; i--)
          {
            path[i-1] = (byte)(node_index % alphabet_size);
          
            if (path[i-1] == 0)
            {
              path[i-1] = alphabet_size;
              node_index /= alphabet_size;
            }

            else
            {
              node_index = node_index / alphabet_size + 1;
            }
          }
        }

        return path;
      }

      private Node GetNode(int depth, int x, int y, int width, int height, int max_depth)
      {
//        int depth = GetDepth(x);

        int node_index = GetNodeIndex(depth, x, y, width, height, max_depth);

        if (node_index < 0)
        {
          Node dummy = new Node(0,0,0);
          return dummy;
        }

//        if (!ZOOM_IN_ORIGINAL)
//        {
//          if (pathZoomedIn.Length > 0 && pathZoomedIn[0] > 0)
//          {
//            depth = depth + pathZoomedIn.Length;
//          }
//        }

        if (depth == 0)
        {
          if (ZOOM_IN_ORIGINAL || (!ZOOM_IN_ORIGINAL && pathZoomedIn[0] == 0))
          {
            pathZoomedIn[0] = 0;
            return t.Root;
          }
        }

        byte[] path = GetPath(depth, node_index);
   
        if (pathZoomedIn.Length > 0 && !ZOOM_IN_ORIGINAL)
        {
          byte[] temp = new byte[pathZoomedIn.Length + path.Length];

          if (pathZoomedIn[0] > 0)
          {
            for (int i = 0; i < pathZoomedIn.Length; i++)
            {
              temp[i] = pathZoomedIn[i];
            }
        
            for (int i = 0; i < path.Length; i++)
            {
              temp[i+pathZoomedIn.Length] = path[i];
            }
          
            pathZoomedIn = temp;
          }

          else
            pathZoomedIn = path;
        }

        else
        {
          pathZoomedIn = path;
        }

        return t.GetNode(pathZoomedIn);
//        if (radioViewSecond.Checked)
//          return t2.GetNode(pathZoomedIn);
//        else
//          return t.GetNode(pathZoomedIn);

      }

      bool bMouseMoved = false;

      private void pbTree_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
      {
        if (t == null)
          return;


        REDRAW = true;

        // Update the mouse path with the mouse information
        Point mouseDownLocation = new Point(e.X, e.Y);
        
        int depth = GetDepth(e.X, pbTree.Width, num_seg);

        ZOOM_IN_ORIGINAL = true;

        // draw the subtree rooted on the node being clicked
        // Special case when depth = 0: it could be either the root node or a branch
        // from the root node
        if (depth >= 0)
        {
          Node curNode = GetNode(depth, e.X, e.Y, pbTree.Width, pbTree.Height, num_seg);


          if (curNode.children.Length > 0)
            DrawTree(curNode, pbZoomIn);

          else
          {
            int child_index = 0;

            child_index = GetClosestBranch(t.Root, e.X, e.Y);

            if (radioViewFirst.Checked || radioViewDiff.Checked)
            {
              PlotSubsequences(t.Root, child_index, alData);
            }
            else if (radioViewSecond.Checked)
            {
              PlotSubsequences(t.Root, child_index, alData2);
            }
          }
        }
    
        else if (depth < 0)
        {
          depth = 0 - depth;      // get the positive (we negated it on purpose to distinguish
                        // between nodes and branches)
          
          Node parentNode = GetParent(depth, e.Y, pbTree.Height); 

          // If the parent node doesn't exist (null)
          if (parentNode.data == 0 && depth != 1)
            return;

          int child_index = GetClosestBranch(parentNode, e.X, e.Y);

          if (radioViewFirst.Checked || radioViewDiff.Checked)
            PlotSubsequences(parentNode, child_index, alData);

          else if (radioViewSecond.Checked)
            PlotSubsequences(parentNode, child_index, alData2);
        }

      }


      private void pbZoomIn_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
      {
        if (t == null)
          return;
//        REDRAW = true;

        // Update the mouse path with the mouse information
        Point mouseDownLocation = new Point(e.X, e.Y);

        int max_dep = 0;

        if (pathZoomedIn.Length == 0)
        {
          max_dep = num_seg;
        }
        else
        {
          if (pathZoomedIn[0] == 0)
            max_dep = num_seg;
          else
            max_dep = num_seg - pathZoomedIn.Length;
        }

        int depth = GetDepth(e.X, pbZoomIn.Width, max_dep);

        ZOOM_IN_ORIGINAL = false;

        // draw the subtree rooted on the node being clicked
        // Special case when depth = 0: it could be either the root node or a branch
        // from the root node
        if (depth >= 0)
        {
//          depth += pathZoomedIn.Length;

          Node curNode = GetNode(depth, e.X, e.Y, pbZoomIn.Width, pbZoomIn.Height, max_dep);


          if (curNode.children.Length > 0)
            DrawTree(curNode, pbZoomIn);
          else
          {
            int child_index;

            // if the zoomed in node was the root
            if (max_dep == num_seg)
            {
              child_index = GetClosestBranch(t.Root, e.X, e.Y);

              if (radioViewFirst.Checked)
              {
                PlotSubsequences(t.Root, child_index, alData);
              }

              else if (radioViewSecond.Checked)
              {
                PlotSubsequences(t.Root, child_index, alData2);
              }

              else
                PlotSubsequences(t.Root, child_index, alData);
            }

            // if the zoomed in node was anything other than the root
            else
            {
              Node parentNode = GetParent(depth, e.Y, pbZoomIn.Height); 
              child_index = GetClosestBranch(parentNode, e.X, e.Y);

              if (radioViewFirst.Checked)
                PlotSubsequences(parentNode, child_index, alData);
              else if (radioViewSecond.Checked)
                PlotSubsequences(parentNode, child_index, alData2);
              else
                PlotSubsequences(parentNode, child_index, alData);
            }
          }
        }
    
        else if (depth < 0)
        {
          depth = 0 - depth;      // get the positive (we negated it on purpose to distinguish
          // between nodes and branches)
          
          Node parentNode = GetParent(depth, e.Y, pbZoomIn.Height); 

          // If the parent node doesn't exist (null)
          if (parentNode.data == 0 && depth != 1)
            return;

          int child_index = GetClosestBranch(parentNode, e.X, e.Y);

          if (radioViewFirst.Checked)
            PlotSubsequences(parentNode, child_index, alData);
          else if (radioViewSecond.Checked)
            PlotSubsequences(parentNode, child_index, alData2);
          else
            PlotSubsequences(parentNode, child_index, alData);

        }

      }

      // Calculate the perpendicular distance from a point (C) to a line (AB)
      private double GetPerpDistance(double ax, double ay,
        double bx, double by,
        double cx, double cy)
      {
        // Suppose we draw a perpendicular line from point C to line AB.  Let the intersection
        // be point D.  We can use the laws of cosines to find cos(A):
        //
        //  BC^2 = AC^2 + AB^2 - 2(AC)(AB)cos(A)
        //  cos(A) = (AC^2 + AB^2 - BC^2) / 2(AC)(AB)
        //
        // Once we find cos(A), we can find angle A.  Then we can calculate CD by:
        //
        // sin(A) = CD / AC
        // CD = AC * sin(A)
        //

        // These are squared Euclidean distances
        double dist_AC = Math.Pow((ax-cx),2) + Math.Pow((ay-cy),2);
        double dist_AB = Math.Pow((ax-bx),2) + Math.Pow((ay-by),2);
        double dist_BC = Math.Pow((bx-cx),2) + Math.Pow((by-cy),2);

        double angle_A = Math.Acos((dist_AC + dist_AB - dist_BC) / 
          (2 * Math.Sqrt(dist_AC) * Math.Sqrt(dist_AB)));

        return (Math.Sqrt(dist_AC) * Math.Sin(angle_A));
      }

      private void buttonOption_Click(object sender, System.EventArgs e)
      {
        Op = new Options(NR_option, NORM);
        Op.Show();
//        NR_option = Op.NR_OPT; 

    //    Op.Close();
      }

      private void menuAdd_Click(object sender, System.EventArgs e)
      {
        if (dlgOpenFile.ShowDialog() == DialogResult.OK)
        {
          String filename = dlgOpenFile.FileName;
        
          s2 = new SuffixTree();

    //      alData2 = new ArrayList();
          alData2 = s2.LoadFile(filename);    

          max_len = (alData.Count > alData2.Count ? alData.Count : alData2.Count);

          //        ArrayList alSymbol = x.BuildMatrix(alData, 4, 4);

          //  BuildTree(alSymbol, 4);
        
          labelFilename2.Text = filename;

          ArrayList dummy = new ArrayList();

          added_second = true;

          buttonLeft.Enabled = false;

          SCROLL_INCREMENT = 100;

          scroll = 0;
    //      plot_scale = max_len / this.pbTS.Width;
          plot_scale = (this.pbTS.Width * 0.98) / max_len;


    //      plot_scale = DEFAULT_SCALE;
    //      DEFAULT_SCALE = 0.3;

          Plot(dummy, 0);

          radioViewSecond.Enabled = true;
          radioViewDiff.Enabled = true;
          radioViewDiff.Checked = true;

          //        this.groupBoxParam.Enabled = true;
//          this.buttonShowTree.Enabled = true;

//          this.menuAdd.Enabled = true;
        
          Refresh(pbTree);
          Refresh(pbZoomIn);
          Refresh(pbSubseq);
          curOffset.Clear();
          surprising.Clear();
          scores.Clear();

          
        }
      }

      private void buttonAddData_Click(object sender, System.EventArgs e)
      {
        if (dlgOpenFile.ShowDialog() == DialogResult.OK)
        {
          String filename = dlgOpenFile.FileName;
        
          s2 = new SuffixTree();

    //      alData2 = new ArrayList();

          alData2 = s2.LoadFile(filename);    

          max_len = (alData.Count > alData2.Count ? alData.Count : alData2.Count);

          //        ArrayList alSymbol = x.BuildMatrix(alData, 4, 4);

          //  BuildTree(alSymbol, 4);
          
          labelFilename2.Text = filename;

          ArrayList dummy = new ArrayList();

          added_second = true;

          buttonLeft.Enabled = false;
          SCROLL_INCREMENT = 100;
          scroll = 0;
    //      plot_scale = DEFAULT_SCALE;
    //      plot_scale = max_len / this.pbTS.Width;

          plot_scale = (this.pbTS.Width * 0.98) / max_len;

    //      DEFAULT_SCALE = 0.3;

          Plot(dummy, 0);

          //        this.groupBoxParam.Enabled = true;
          //        this.buttonShowTree.Enabled = true;

          //        this.menuAdd.Enabled = true;

          radioViewSecond.Enabled = true;
          radioViewDiff.Enabled = true;
          radioViewDiff.Checked = true;

          Refresh(pbTree);
          Refresh(pbZoomIn);
          Refresh(pbSubseq);
          curOffset.Clear();
          surprising.Clear();
          scores.Clear();

        }
      }

      private void menuPrint_Click(object sender, System.EventArgs e)
      {
        PrintDialog dlg = new PrintDialog();
        dlg.Document = printDoc;
        if (dlg.ShowDialog() == DialogResult.OK) 
        {
          printDoc.Print();
        }

      }

      // Normalize the time series so it has mean 0 and standard deviation 1
      public double[] Normalize(double[] time_series)
      {
        double mean = Mean(time_series);
        double std = StdDev(time_series);

        if (std == 0)
          std = 0.0001;

        double[] normalized = new double[time_series.Length];

        for (int i = 0; i < time_series.Length; i++)
        {
    //      if (std != 0)
            normalized[i] = (time_series[i] - mean) / std;
        }

        return normalized;
      }

      // Compute the standard deviation of the time series
      public double StdDev(double[] time_series)
      {
        double mean = Mean(time_series);
        double var = 0.0;

        for (int i = 0; i < time_series.Length; i++)
        {
          var += Math.Pow((time_series[i] - mean), 2);
        }
        
        var /= (time_series.Length - 1);

        return Math.Sqrt(var);
      }

      // Calculate the average of any section of the data
      public double Mean(double[] data, int index1, int index2)
      {
        //try
        //{
        if (index1 < 0 || index2 < 0 || index1 >= data.Length ||
          index2 >= data.Length)
        {
          throw new Exception("Invalid index!");
        }
        //}
        
        if (index1 > index2)
        {
          int temp = index2;
          index2 = index1;
          index1 = temp;
        }

        double sum = 0;

        for (int i = index1; i <= index2; i++)
        {
          sum += data[i];
        }

        return sum / (index2 - index1 + 1);
      }


      private void buttonReset_Click(object sender, System.EventArgs e)
      {
        Refresh(this.pbTS);
        Refresh(this.pbTree);
        Refresh(this.pbSubseq);
        Refresh(this.pbZoomIn);

        Initialize();
      }

      private void buttonZoomOut_Click(object sender, System.EventArgs e)
      {

        plot_scale /= 1.5;

      //  int start_pos = scroll * SCROLL_INCREMENT;
        end_pos = (int)(start_pos + this.pbTS.Width / plot_scale + 1);

        if (end_pos >= max_len)
          end_pos = max_len - 1;

        buttonZoomIn.Enabled = true;

        int interval = (end_pos - start_pos + 1) / 4;

        if (interval == 0)
        {
          interval = 1;
          buttonZoomIn.Enabled = false;
        }

        SCROLL_INCREMENT = interval;

        Plot(curOffset, curWeight);

        zoom_level--;

        // disable zoom-out if it's already at the original scale
        if (zoom_level == 0)
        {
          buttonZoomOut.Enabled = false;
          return;
        }
      }

      private void buttonZoomIn_Click(object sender, System.EventArgs e)
      {
        plot_scale *= 1.5;

        //if (plot_scale > 20)
        //{
        //  buttonZoomIn.Enabled = false;
        //  return;
        //}

        buttonZoomOut.Enabled = true;

      //  start_pos = scroll * SCROLL_INCREMENT;
        end_pos = (int)(start_pos + this.pbTS.Width / plot_scale + 1);

        int interval = (end_pos - start_pos + 1) / 4;

        if (interval == 0)
        {
          interval = 1;
          buttonZoomIn.Enabled = false;
        }

        SCROLL_INCREMENT = interval;

        Plot(curOffset, curWeight);

        zoom_level++;
      }

      private void buttonLeft_Click(object sender, System.EventArgs e)
      {
        scroll--;

        buttonRight.Enabled = true;

    //    if (scroll == 0)
    //      buttonLeft.Enabled = false;


        //start_pos = scroll * SCROLL_INCREMENT;
        
        start_pos -= SCROLL_INCREMENT;

        if (start_pos < 0)
        {
          start_pos = 0;
          scroll = 0;
          buttonLeft.Enabled = false;
        }

        Plot(curOffset, curWeight);

      }

      private void buttonRight_Click(object sender, System.EventArgs e)
      {
        scroll++;
        
        start_pos = scroll * SCROLL_INCREMENT;

        buttonLeft.Enabled = true;

        Plot(curOffset, curWeight);

        if ((start_pos + SCROLL_INCREMENT) > max_len)
          buttonRight.Enabled = false;
      }

      public double Mean(double[] time_series)
      {
        return Mean(time_series, 0, time_series.Length-1);
      }

      

    }
}
