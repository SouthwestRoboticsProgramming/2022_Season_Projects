from flask import render_template, redirect, url_for, flash, request
from app import appvar, db, ALLOWED_EXTENSIONS
from .forms import SearchForm, LoginForm, RegistrationForm

from werkzeug.urls import url_parse
from flask_login import current_user, login_user
from app.models import User
from flask_login import logout_user, login_required
import os
from werkzeug.utils import secure_filename

@appvar.route('/login', methods=['GET', 'POST'])
def login():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    form = LoginForm()
    if form.validate_on_submit():
        user = User.query.filter_by(username=form.username.data).first()
        if user is None or not user.check_password(form.password.data):
            flash('Invalid username or password')
            return redirect(url_for('login'))
        login_user(user, remember=form.remember_me.data)
        next_page = request.args.get('next')
        if not next_page or url_parse(next_page) != '':
            next_page = url_for('index')
        return redirect(next_page)
    return render_template('login.html', title='Sign In', form=form)

@appvar.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('index'))

@appvar.route('/register', methods=['GET', 'POST'])
def register():
    if current_user.is_authenticated:
        return redirect(url_for('index'))
    form = RegistrationForm()
    if form.validate_on_submit():
        user = User(username=form.username.data, email=form.email.data)
        user.set_password(form.password.data)
        db.session.add(user)
        db.session.commit()
        flash('Congratulations, you are now a registered user!')
        return redirect(url_for('login'))
    return render_template('register.html', title='Register', form=form)


@appvar.route('/')
@appvar.route('/index')
def index():
    return render_template('index.html', title='Home')

@appvar.route('/secret')
@login_required
def hidden_page():
    return render_template('secret.html', title="Super Secret", 
    user=current_user)

def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@appvar.route('/upload', methods=['GET', 'POST'])
@login_required
def upload_page():
    if request.method == "POST":
        if 'file' not in request.files:
            flash("No file attached")
            return redirect(request.url)
        
        file = request.files['file']

        if file.filename == "":
            flash("No file attached")
            return redirect(request.url)
        
        if file and allowed_file(file.filename):
            flash("Successfully uploaded!")
            flash(file.filename)
            
            ## Consider saving the file
            # fname, ext = os.path.splitext(file.filename)
            # fname_rand = fname + str(uuid.uuid4()) + ext
            # filename = secure_filename(fname_rand)
            # file.save(os.path.join(appvar.config["UPLOAD_FOLDER"], filename))
            
    return render_template('upload.html', title="Upload Form Example")