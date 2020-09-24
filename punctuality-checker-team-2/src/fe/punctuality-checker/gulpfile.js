const fileinclude = require('gulp-file-include');
const gulp = require('gulp');
var concat = require('gulp-concat');

gulp.task('fileinclude', async function() {
  gulp.src(['./src/html/pages/*.html'])
    .pipe(fileinclude({
      prefix: '@@',
      basepath: '@file'
    }))
    .pipe(gulp.dest('./'));
});

gulp.task('scripts', function() {
  return gulp.src([
    "./src/data/dataFinger.js",
    "./src/js/login.js",
    "./src/js/daily.js",
    "./src/js/range.js",
    "./src/js/employees.js"])
    .pipe(concat('main.js'))
    .pipe(gulp.dest('./src/public/js'));
});

gulp.task('watch', function() {
    gulp.watch('./src/html/**/*.html', gulp.series(['fileinclude']));
    gulp.watch('./src/js/*.js', gulp.series(['scripts']));
});
